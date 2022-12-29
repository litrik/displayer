package com.displayer.app

import com.displayer.config.ConfigRepo
import com.displayer.weather.WeatherRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class App(
    private val configRepo: ConfigRepo,
    private val displayRepo: com.displayer.display.DisplayRepo,
    private val weatherRepo: WeatherRepo,
) {

    private val state = MutableStateFlow<AppState>(AppState.Initializing)

    init {
        GlobalScope.launch {
            combine(
                displayRepo.observeState(),
                weatherRepo.observeState(),
            ) { displayState, weatherState ->
                AppState.Ready(
                    displayState = displayState,
                    weatherState = weatherState
                )
            }.collect { state.value = it }
        }
    }

    fun observeState(): Flow<AppState> = state

    fun setOpenWeatherApiKey(apiKey: String) = configRepo.setOpenWeatherApiKey(apiKey)

    suspend fun loadDisplay(url: String?) = displayRepo.loadDisplay(url)

}
