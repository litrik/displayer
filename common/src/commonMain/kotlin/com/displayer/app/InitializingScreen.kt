package com.displayer.app

import Strings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.displayer.toSp
import com.displayer.ui.LocalDimensions
import com.displayer.ui.Text
import kotlinx.coroutines.delay

@Composable
fun InitializingScreen() {
    var showLoading by remember { mutableStateOf(false) }
    AnimatedVisibility(showLoading, enter = fadeIn()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(Strings.loading.toString(), style = TextStyle.Default.copy(fontSize = LocalDimensions.current.fontSize.toSp()))
        }
    }
    LaunchedEffect(true) {
        delay(1000)
        showLoading = true
    }
}
