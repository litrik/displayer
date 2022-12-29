package com.displayer.weather

import kotlinx.datetime.Instant

sealed class WeatherState {

    object ApiKeyNotSet : WeatherState()

    data class ApiKeyInvalid(val instant: Instant, val apiKey: String?) : WeatherState()

    data class Success(val instant: Instant) : WeatherState()

    data class Failure(val instant: Instant, val exception: Exception) : WeatherState()

}
