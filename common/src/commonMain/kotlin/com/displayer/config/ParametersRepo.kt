package com.displayer.config

import co.touchlab.kermit.Logger
import com.displayer.display.parser.Parser
import de.comahe.i18n4k.config.I18n4kConfigDefault
import de.comahe.i18n4k.i18n4k
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/*
    Manages the parameters specified in a display file
 */
class ParametersRepo {

    private var parametersFlow = MutableStateFlow(Parser.parseParameters(null))

    fun observeParameters(): Flow<Parameters> = parametersFlow

    fun setParameters(parameters: Parameters) {
        parametersFlow.value = parameters
        activateLanguage(parameters.language)
    }

    private fun activateLanguage(language: String?) {
        Logger.i("Setting language to $language")
        val i18n4kConfig = I18n4kConfigDefault()
        i18n4k = i18n4kConfig
        i18n4kConfig.locale = de.comahe.i18n4k.Locale(language)
    }
}
