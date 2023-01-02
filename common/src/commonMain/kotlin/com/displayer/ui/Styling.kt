package com.displayer.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.displayer.display.Style
import com.displayer.display.defaultStyle

val DisplayerPurple = Color(156, 39, 176)

val LocalTextAlign = compositionLocalOf { TextAlign.Left }
val LocalStyle = compositionLocalOf { defaultStyle }
val LocalDirection = compositionLocalOf { Direction.None }
val LocalDimensions = compositionLocalOf { Dimensions() }
val LocalLocale = compositionLocalOf { Locale.current }

enum class Direction {
    None,
    Horizontal,
    Vertical,
}

data class Dimensions(
    val screenWidth: Dp = 1920.dp,
    val screenHeight: Dp = 1080.dp,
) {

    val screenAspectRatio: Float
        get() = screenWidth / screenHeight

    val baseUnit: Dp
        get() = screenHeight / 40 // 1080p -> 42px

    val fontSize: Dp
        get() = baseUnit * 2

    val fontSizeSmall: Dp
        get() = baseUnit * 1.5f

}

@Composable
fun StyledContent(
    style: Style?,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalStyle provides (style ?: LocalStyle.current)) {
        content()
    }
}
