package com.displayer.display

import androidx.compose.ui.text.intl.Locale
import co.touchlab.kermit.Logger
import com.displayer.config.ConfigRepo
import com.displayer.config.ParametersRepo
import com.displayer.display.parser.DisplayFile
import com.displayer.display.parser.Message
import com.displayer.display.parser.Parser
import com.displayer.display.parser.Result
import com.displayer.display.parser.Severity
import com.displayer.platform.getDisplayCachePath
import com.displayer.platform.getFilesystem
import com.displayer.weather.WeatherRepo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.buffer
import okio.use

class DisplayRepo(
    private val httpClient: HttpClient,
    private val parametersRepo: ParametersRepo,
    private val configRepo: ConfigRepo,
    private val weatherRepo: WeatherRepo,
    private val json: Json,
) {

    private val state = MutableStateFlow<DisplayState>(DisplayState.NoDisplay)
    private var refreshJob: Job? = null

    fun observeState(): Flow<DisplayState> = state

    suspend fun loadDisplayFromUrl(url: String?, refreshDelayInMinutes: Int = 0) {
        if (refreshDelayInMinutes == 0) {
            refreshJob?.cancel()
        }
        val messages = mutableListOf<Message>()
        val actualUrl = url ?: configRepo.getDisplayUrl()
        var actualRefreshDelayInMinutes: Int = refreshDelayInMinutes
        if (actualUrl.isNullOrEmpty()) {
            val rememberedFile = loadDisplayFile()
            if (rememberedFile != null) {
                Logger.i { "Initializing Displayer with remembered display" }
                val display = parseDisplayFile(rememberedFile).andReport(messages)
                state.value = DisplayState.Success(
                    messages = messages.toImmutableList(),
                    display = display,
                    url = null,
                )
            } else {
                Logger.i { "Initializing Displayer with default display" }
                messages.add(Message(Severity.Info, "Using default display"))
                val display = parseDisplayFile(defaultDisplayFile).andReport(messages)
                state.value = DisplayState.Success(
                    messages = messages.toImmutableList(),
                    display = display,
                )
            }
        } else {
            Logger.i { "Initializing Displayer with remembered url $actualUrl " }
            try {
                Logger.i { "Loading display from remote url $actualUrl" }
                val file: DisplayFile = httpClient.get(actualUrl).body()
                val display = parseDisplayFile(file).andReport(messages)
                configRepo.setDisplayUrl(actualUrl)
                saveDisplayFile(file)
                actualRefreshDelayInMinutes = display.refreshInMinutes
                state.value = DisplayState.Success(
                    messages = messages.toImmutableList(),
                    display = display,
                    url = actualUrl,
                )
            } catch (e: Exception) {
                Logger.e("Failed to load the display file", e)
                messages.add(Message(Severity.Error, "Failed to parse the display file: ${e::class.simpleName}\n${e.message.orEmpty()}"))
                val rememberedFile = loadDisplayFile()
                if (rememberedFile != null) {
                    Logger.i { "Falling back to remembered display" }
                    val display = parseDisplayFile(rememberedFile).andReport(messages)
                    state.value = DisplayState.Success(
                        messages = messages.toImmutableList(),
                        display = display,
                        url = null,
                    )
                } else {
                    state.value = DisplayState.Failure(actualUrl, messages.toImmutableList())
                }
            }
            scheduleRefresh(actualUrl, actualRefreshDelayInMinutes)
        }
    }

    private fun saveDisplayFile(file: DisplayFile) {
        Logger.d("Saving display file to cache")
        try {
            val s = json.encodeToString(file)
            getFilesystem().sink(getDisplayCachePath()).buffer().use { sink ->
                sink.writeUtf8(s)
                sink.flush()
            }
        } catch (e: Exception) {
            Logger.e("Failed to save display file to cache", e)
        }
    }

    private fun loadDisplayFile(): DisplayFile? {
        Logger.d("Loading display file from cache")
        try {
            getFilesystem().source(getDisplayCachePath()).use { source ->
                val s = source.buffer().readByteString().utf8()
                return json.decodeFromString<DisplayFile>(s)
            }
        } catch (e: Exception) {
            Logger.e("Failed to load display file from cache", e)
        }
        return null
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadDisplayFromJsonString(str: String) {
        val messages = mutableListOf<Message>()
        try {
            val file: DisplayFile = json.decodeFromString(str)
            val display = parseDisplayFile(file).andReport(messages)
            saveDisplayFile(file)
            state.value = DisplayState.Success(
                messages = messages.toImmutableList(),
                display = display,
                url = null,
            )
        } catch (e: Exception) {
            Logger.e("Failed to parse the display file", e)
            messages.add(Message(Severity.Error, "Failed to parse the display file: ${e::class.simpleName}\n${e.message.orEmpty()}"))
            state.value = DisplayState.Failure(null, messages.toImmutableList())
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun scheduleRefresh(url: String, delayInMinutes: Int) {
        if (delayInMinutes > 0) {
            Logger.d("Scheduling refresh of display in $delayInMinutes minutes")
            refreshJob = GlobalScope.launch(Dispatchers.Unconfined) {
                delay(delayInMinutes * 60_000L)
                Logger.d("Refreshing display")
                loadDisplayFromUrl(url, delayInMinutes)
            }
        }
    }

    private fun parseDisplayFile(dto: DisplayFile): Result<Display> {
        Logger.i("Parsing display file")
        val messages = mutableListOf<Message>()

        val parameters = Parser.parseParameters(dto.parameters)
        parametersRepo.setParameters(parameters)

        val allStyles = dto.styles.map { Parser.parseStyle(it).andReport(messages) }

        val resolveStyle = { styleId: String?, context: String -> resolveStyle(styleId, context, allStyles).andReport(messages) }
        val style = resolveStyle(dto.styleId, "Display")
        val center = Parser.parseContainer(
            dto = dto.center,
            context = "Center region",
            resolveStyle = resolveStyle,
            observeCurrentWeather = weatherRepo.getCurrentWeatherProvider(),
            true,
        ).andReport(messages)
        val left = Parser.parseContainer(
            dto = dto.left,
            context = "Left region",
            resolveStyle = resolveStyle,
            observeCurrentWeather = weatherRepo.getCurrentWeatherProvider(),
        ).andReport(messages)
        val bottom = Parser.parseContainer(
            dto = dto.bottom,
            context = "Bottom region",
            resolveStyle = resolveStyle,
            observeCurrentWeather = weatherRepo.getCurrentWeatherProvider(),
        ).andReport(messages)

        if (dto.left != null && (dto.bottom == null || dto.bottom.items.isEmpty())) {
            messages.add(Message(Severity.Warning, "Left region is present but bottom region is missing/empty"))
        }
        if (dto.bottom != null && (dto.left == null || dto.left.items.isEmpty())) {
            messages.add(Message(Severity.Warning, "Bottom region is present but left region is missing/empty"))
        }

        if (messages.all { it.severity == Severity.Info }) {
            Logger.i("Display file parsed successfully")
        } else {
            val issuesText = listOf(
                Severity.Error,
                Severity.Warning,
            )
                .associateWith { severity -> messages.count { it.severity == severity } }
                .filter { it.value > 0 }
                .map { "${it.value} ${it.key.label}" }.joinToString(" and ")
            messages.add(Message(Severity.Info, "Display file contains $issuesText"))
            messages.onEach {
                when (it.severity) {
                    Severity.Warning -> Logger.w(it.message)
                    Severity.Error -> Logger.e(it.message)
                    else -> Logger.i(it.message)
                }
            }
        }

        return Result(
            data = Display(
                locale = Locale("${parameters.language}-${parameters.country}"),
                refreshInMinutes = dto.parameters?.refreshInMinutes ?: 0,
                style = style ?: defaultStyle,
                center = center,
                left = left,
                bottom = bottom,
                bottomHeight = if (left.items.isEmpty() && bottom.items.isEmpty()) 0f else (dto.bottomHeight ?: Display.DEFAULT_BOTTOM_HEIGHT)
            ),
            messages = messages,
        )
    }

    private fun resolveStyle(styleId: String?, context: String, allStyles: List<Style>): Result<Style?> {
        val messages = mutableListOf<Message>()
        val style = allStyles.firstOrNull { it.id == styleId }.also {
            if (styleId != null && it == null) {
                messages.add(Message(Severity.Error, "$context uses unknown style '$styleId'"))
            }
        }
        return Result(style, messages)
    }

}
