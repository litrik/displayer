package com.displayer.config

import com.displayer.display.parser.Units

data class Parameters(
    val language: String,
    val country: String,
    val zip: String? = null,
    val units: Units = Units.Metric,
)
