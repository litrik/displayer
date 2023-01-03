package com.displayer.app

import co.touchlab.kermit.Logger
import com.displayer.admin.AdminServer
import com.displayer.config.AdminParameters
import com.displayer.config.ConfigRepo
import com.displayer.display.DisplayRepo
import com.displayer.weather.WeatherRepo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.InputStream

class App(
    private val configRepo: ConfigRepo,
    private val displayRepo: DisplayRepo,
    private val weatherRepo: WeatherRepo,
    private val adminServer: AdminServer,
) {

    private val state = MutableStateFlow<AppState>(AppState.Initializing)

    init {
        GlobalScope.launch {
            combine(
                displayRepo.observeState(),
                weatherRepo.observeState(),
                adminServer.observeState(),
            ) { displayState, weatherState, adminState ->
                AppState.Ready(
                    displayState = displayState,
                    weatherState = weatherState,
                    adminState = adminState,
                )
            }.collect { state.value = it }
        }

    }

    fun observeState(): Flow<AppState> = state

    fun setOpenWeatherApiKey(apiKey: String) = configRepo.setOpenWeatherApiKey(apiKey)

    suspend fun loadDisplay(url: String?) = displayRepo.loadDisplay(url)

    suspend fun loadDisplay(stream: InputStream) = displayRepo.loadDisplay(stream)

    fun setAdminParameters(port: Int?, secret: String?) {
        if (port == null) {
            Logger.e("Port of admin server is missing")
            return
        }
        if (port <= 0 || port > 65535) {
            Logger.e("Port of admin server is invalid: $port")
            return
        }
        if (secret.isNullOrBlank()) {
            Logger.e("Secret of admin server is missing")
            return
        }
        configRepo.setAdminParameters(AdminParameters(port, secret))
    }

    fun stopAdminServer() {
        configRepo.setAdminParameters(null)
    }

}
