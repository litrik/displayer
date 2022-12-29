package com.displayer.display.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.displayer.platform.formatTime
import com.displayer.ui.LocalLocale
import kotlinx.coroutines.delay

data class Clock(
    override val style: com.displayer.display.Style? = null,
    override val padding: com.displayer.display.Padding = com.displayer.display.Padding(),
) : Item()

@Composable
fun ClockUi() {
    var timeString by remember { mutableStateOf("") }
    val locale = LocalLocale.current
    LaunchedEffect(true) {
        while (true) {
            timeString = formatTime(kotlinx.datetime.Clock.System.now(), locale)
            delay(200)
        }
    }
    AutoFitText(text = timeString)
}
