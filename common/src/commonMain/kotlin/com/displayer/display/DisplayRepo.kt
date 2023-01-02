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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.InputStream

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

    suspend fun loadDisplay(url: String?, refreshDelayInMinutes: Int = 0) {
        if (refreshDelayInMinutes == 0) {
            refreshJob?.cancel()
        }
        val messages = mutableListOf<Message>()
        val actualUrl = url ?: configRepo.getDisplayUrl()
        var actualRefreshDelayInMinutes: Int = refreshDelayInMinutes
        if (actualUrl.isNullOrEmpty()) {
            val rememberedFile = configRepo.getDisplayFile()
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
                val file: DisplayFile =
                    if (actualUrl.startsWith("https")) {
                        Logger.i { "Loading display from remote url $actualUrl" }
                        httpClient.get(actualUrl).body()
                    } else {
                        Logger.i { "Loading display from local file $actualUrl" }
                        json.decodeFromString(File(actualUrl).readText())
                    }
                val display = parseDisplayFile(file).andReport(messages)
                configRepo.setDisplayUrl(actualUrl)
                actualRefreshDelayInMinutes = display.refreshInMinutes
                state.value = DisplayState.Success(
                    messages = messages.toImmutableList(),
                    display = display,
                    url = actualUrl,
                )
            } catch (e: Exception) {
                Logger.e("Failed to parse the display file", e)
                messages.add(Message(Severity.Error, "Failed to parse the display file: ${e.javaClass.simpleName}\n${e.message.orEmpty()}"))
                state.value = DisplayState.Failure(actualUrl, messages.toImmutableList())
            }
            scheduleRefresh(actualUrl, actualRefreshDelayInMinutes)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadDisplay(stream: InputStream) {
        stream.use {
            val messages = mutableListOf<Message>()
            try {
                val file: DisplayFile = json.decodeFromStream(it)
                val display = parseDisplayFile(file).andReport(messages)
                configRepo.setDisplayFile(file)
                state.value = DisplayState.Success(
                    messages = messages.toImmutableList(),
                    display = display,
                    url = null,
                )
            } catch (e: Exception) {
                Logger.e("Failed to parse the display file", e)
                messages.add(Message(Severity.Error, "Failed to parse the display file: ${e.javaClass.simpleName}\n${e.message.orEmpty()}"))
                state.value = DisplayState.Failure(null, messages.toImmutableList())
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun scheduleRefresh(url: String, delayInMinutes: Int) {
        if (delayInMinutes > 0) {
            Logger.d("Scheduling refresh of display in $delayInMinutes minutes")
            refreshJob = GlobalScope.launch(Dispatchers.Unconfined) {
                delay(delayInMinutes * 60_000L)
                Logger.d("Refreshing display")
                loadDisplay(url, delayInMinutes)
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
