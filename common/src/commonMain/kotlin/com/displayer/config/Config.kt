package com.displayer.config

import com.displayer.display.parser.DisplayFile
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val displayUrl: String? = null,
    val displayFile: DisplayFile? = null,
    val openWeatherApiKey: String? = null,
)
