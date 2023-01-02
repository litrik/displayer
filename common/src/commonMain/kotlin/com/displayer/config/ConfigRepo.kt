package com.displayer.config

import co.touchlab.kermit.Logger
import com.displayer.display.parser.DisplayFile
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/*
    Manages/persists the configuration of the app
 */
class ConfigRepo(
    private val json: Json,
    private val settings: Settings,
) {

    private var configFlow = MutableStateFlow(json.decodeFromString<Config>(settings.getStringOrNull(KEY_CONFIG) ?: "{}"))

    fun observeConfig(): Flow<Config> = configFlow

    private fun updateConfig(config: Config) {
        configFlow.value = config
        settings.putString(KEY_CONFIG, json.encodeToString(config))
    }

    fun setOpenWeatherApiKey(apiKey: String) {
        Logger.d("Setting OpenWeather API key to $apiKey")
        updateConfig(configFlow.value.copy(openWeatherApiKey = apiKey))
    }

    fun getDisplayUrl(): String? = configFlow.value.displayUrl

    fun setDisplayUrl(url: String) {
        updateConfig(configFlow.value.copy(displayUrl = url, displayFile = null))
    }

    fun setDisplayFile(file: DisplayFile) {
        updateConfig(configFlow.value.copy(displayUrl = null, displayFile = file))
    }

    fun getDisplayFile(): DisplayFile? = configFlow.value.displayFile

    companion object {
        const val KEY_CONFIG = "Displayer.Config"
    }

}
