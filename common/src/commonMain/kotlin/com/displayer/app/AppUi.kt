@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)

package com.displayer.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import com.displayer.admin.AdminPanel
import com.displayer.display.DisplayScreen
import com.displayer.ui.Dimensions
import com.displayer.ui.LocalDimensions
import com.displayer.ui.LocalStyle

@Composable
fun AppUi(state: AppState) {
    var showAdminPanel by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .onFocusChanged {
                hasFocus = it.hasFocus
            }
            .focusable()
            .background(LocalStyle.current.backgroundColor)
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { showAdminPanel = !showAdminPanel })
            }
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp) {
                    if (it.key == Key.DirectionCenter) {
                        showAdminPanel = !showAdminPanel
                        return@onKeyEvent true
                    }
                }
                return@onKeyEvent false
            },
        contentAlignment = Alignment.CenterEnd
    ) {
        CompositionLocalProvider(LocalDimensions provides Dimensions(maxWidth, maxHeight)) {
            Crossfade(targetState = state) {
                when (it) {
                    AppState.Initializing -> InitializingScreen()
                    is AppState.Ready -> DisplayScreen(it.displayState)
                }
            }
            AnimatedVisibility(showAdminPanel, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.5f)))
            }
            AnimatedVisibility(showAdminPanel, enter = slideInHorizontally(initialOffsetX = { (it * 1.5f).toInt() }), exit = slideOutHorizontally(targetOffsetX = { (it * 1.5f).toInt() })) {
                AdminPanel(state)
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}



