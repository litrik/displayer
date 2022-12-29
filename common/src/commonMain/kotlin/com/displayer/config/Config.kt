package com.displayer.config

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val displayUrl: String? = null,
    val openWeatherApiKey: String? = null,
)
