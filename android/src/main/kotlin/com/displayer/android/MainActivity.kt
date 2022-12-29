package com.displayer.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import co.touchlab.kermit.Logger
import com.displayer.app.App
import com.displayer.app.AppState
import com.displayer.app.AppUi
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val app by inject<App>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            val display by app.observeState().collectAsState(AppState.Initializing)
            AppUi(display)
        }
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) {
            Logger.w { "Ignoring null Intent" }
            return
        }
        Logger.i { "Handling intent with action ${intent.action} and data ${intent.data}" }
        when (intent.action) {
            Intent.ACTION_MAIN -> {
                lifecycleScope.launchWhenStarted { app.loadDisplay(null) }
            }

            Intent.ACTION_VIEW -> {
                lifecycleScope.launchWhenStarted { app.loadDisplay(intent.data?.toString()) }
            }

            "com.displayer.action.CONFIG" -> {
                intent.extras?.let { handleConfigIntent(it) }
            }
        }
    }

    private fun handleConfigIntent(extras: Bundle) {
        Logger.d { "Handling config intent" }
        extras.getString("com.displayer.extra.OPEN_WEATHER_API_KEY")?.let { app.setOpenWeatherApiKey(it) }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}
