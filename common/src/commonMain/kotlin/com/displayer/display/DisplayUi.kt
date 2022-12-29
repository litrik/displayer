package com.displayer.display

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable

@Composable
fun DisplayScreen(state: DisplayState) {
    Crossfade(targetState = state) {
        when (it) {
            is DisplayState.Success -> MainUi(it.display)
            is DisplayState.Failure -> ErrorUi(it)
            DisplayState.NoDisplay -> {}
        }
    }
}
