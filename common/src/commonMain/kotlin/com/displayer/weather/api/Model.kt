package com.displayer.weather.api

import kotlinx.serialization.Serializable

@Serializable
data class WeatherDataDto(
    val dt: Long,
    val main: MainDto,
    val wind: WindDto,
    val weather: List<ConditionDto> = emptyList()
)

@Serializable
data class MainDto(
    val temp: Float,
)

@Serializable
data class WindDto(
    val speed: Float,
    val deg: Float,
)

@Serializable
data class ConditionDto(
    val description: String,
    val icon: String,
)

@Serializable
data class ForecastResponse(
    val list: List<WeatherDataDto> = emptyList(),
)
