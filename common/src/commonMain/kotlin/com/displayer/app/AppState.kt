package com.displayer.app

import com.displayer.admin.AdminState
import com.displayer.display.DisplayState
import com.displayer.weather.WeatherState

sealed class AppState {

    object Initializing : AppState()

    data class Ready(
        val weatherState: WeatherState,
        val displayState: DisplayState,
        val adminState: AdminState,
    ) : AppState()

}
