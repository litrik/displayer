@file:OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)

package com.displayer.weather

import co.touchlab.kermit.Logger
import com.displayer.config.ConfigRepo
import com.displayer.config.ParametersRepo
import com.displayer.display.parser.Units
import com.displayer.weather.api.ForecastResponse
import com.displayer.weather.api.WeatherDataDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class WeatherRepo(
    private val parametersRepo: ParametersRepo,
    private val configRepo: ConfigRepo,
    private val httpClient: HttpClient,
) {

    private val baseUrl = "https://api.openweathermap.org/data/2.5"
    private val forecastCount = 2

    private val stateFlow = MutableStateFlow<WeatherState>(WeatherState.ApiKeyNotSet)
    private val _currentWeatherFlow = MutableSharedFlow<List<WeatherData>>(replay = 1)
    private val currentWeatherFlow: SharedFlow<List<WeatherData>> = _currentWeatherFlow.asSharedFlow()

    init {
        GlobalScope.launch {
            combine(
                configRepo.observeConfig().filter { it.openWeatherApiKey != null },
                parametersRepo.observeParameters().filter { it.zip != null }
            ) { config, parameters ->
                WeatherParameters(
                    openWeatherApiKey = config.openWeatherApiKey,
                    country = parameters.country,
                    zip = parameters.zip,
                    language = parameters.language,
                    units = parameters.units,
                )
            }
                .flatMapLatest {
                    if (it.openWeatherApiKey == null) {
                        Logger.d { "No OpenWeather API key. Clearing weather cache." }
                        stateFlow.value = WeatherState.ApiKeyNotSet
                        _currentWeatherFlow.resetReplayCache()
                        emptyFlow()
                    } else {
                        Logger.d { "OpenWeather API key found." }
                        pollWeatherFromOpenWeather(it)
                    }
                }.collect {
                    _currentWeatherFlow.emit(it)
                }
        }
    }

    fun observeState() : Flow<WeatherState> = stateFlow

    fun getCurrentWeatherProvider(): () -> Flow<List<WeatherData>> = { currentWeatherFlow }

    private suspend fun pollWeatherFromOpenWeather(parameters: WeatherParameters): Flow<List<WeatherData>> = flow {
        val delayMinutes = 15
        Logger.i { "Polling OpenWeather every $delayMinutes minutes" }
        while (true) {
            try {
                val dataList = mutableListOf<WeatherData>()
                val currentWeatherUrl = URLBuilder("$baseUrl/weather?").apply {
                    this.parameters.append("lang", parameters.language)
                    this.parameters.append("APPID", parameters.openWeatherApiKey!!)
                    this.parameters.append("zip", "${parameters.zip},${parameters.country}")
                    this.parameters.append("units", parameters.units.toString())
                }.build()
                Logger.d { "Getting current weather from $currentWeatherUrl" }
                val currentWeatherDto: WeatherDataDto = httpClient.get(currentWeatherUrl).body()
                dataList.add(WeatherData.fromDto(currentWeatherDto, parameters.units))

                val forecastUrl = URLBuilder("$baseUrl/forecast?&lang=nl").apply {
                    this.parameters.append("APPID", parameters.openWeatherApiKey!!)
                    this.parameters.append("zip", "${parameters.zip},${parameters.country}")
                    this.parameters.append("units", parameters.units.toString())
                }.build()
                Logger.d { "Getting forecast from $forecastUrl" }
                val forecast: ForecastResponse = httpClient.get(forecastUrl).body()
                dataList.addAll(forecast.list.filter { it.dt > currentWeatherDto.dt + 30 * 60 }.take(forecastCount).map { WeatherData.fromDto(it, parameters.units) })

                stateFlow.value = WeatherState.Success(Clock.System.now())
                emit(dataList)
            } catch (e: Exception) {
                if ((e as? ClientRequestException)?.response?.status == HttpStatusCode.Unauthorized) {
                    stateFlow.value = WeatherState.ApiKeyInvalid(Clock.System.now(), parameters.openWeatherApiKey)
                } else {
                    stateFlow.value = WeatherState.Failure(Clock.System.now(), e)
                }
            }
            delay(delayMinutes * 60_000L)
        }
    }

    data class WeatherParameters(
        val openWeatherApiKey: String? = null,
        val country: String,
        val zip: String?,
        val language: String,
        val units: Units = Units.Metric,
    )
}
