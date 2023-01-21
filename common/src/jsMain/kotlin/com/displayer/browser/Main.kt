package com.displayer.browser

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import co.touchlab.kermit.Logger
import com.displayer.app.App
import com.displayer.app.AppState
import com.displayer.app.AppUi
import com.displayer.coreModule
import com.displayer.jsModule
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.startKoin

private val koin = startKoin {
    modules(coreModule, jsModule)
}.koin

fun main() {
    onWasmReady {
        val app = koin.get<App>()
        browserViewportWindow("Displayer") {
            Logger.i("FIXME")
            val appState by app.observeState().collectAsState(AppState.Initializing)
            AppUi(appState)
        }
    }
}
