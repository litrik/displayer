package com.displayer.ui

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle? = TextStyle.Default,
) {
    val themedStyle = TextStyle.Default.copy(
        color = LocalStyle.current.contentColor,
        textAlign = LocalTextAlign.current,
    ).merge(style)
    BasicText(
        text = text,
        modifier = modifier,
        style = themedStyle
    )
}
