package com.displayer.display

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.intl.Locale
import com.displayer.display.container.Container

data class Display(
    val locale : Locale,
    val refreshInMinutes: Int = 0,
    val style: Style,
    val center: Container,
    val left: Container,
    val bottom: Container,
    val bottomHeight: Float,
) {
    companion object {
        val DEFAULT_BOTTOM_HEIGHT: Float = 6f
    }

}

data class Style(
    val id: String,
    val backgroundColor: Color,
    val contentColor: Color,
)

val defaultStyle = Style(
    id = "displayer.style.dark",
    backgroundColor = Color.Black,
    contentColor = Color.White,
)

data class Padding(
    val horizontal: Float = 0f,
    val vertical: Float = 0f,
)
