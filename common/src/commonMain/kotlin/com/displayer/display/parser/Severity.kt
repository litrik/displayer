package com.displayer.display.parser

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import com.displayer.platform.getIcon
import com.displayer.ui.Icon
import com.displayer.ui.LocalStyle

enum class Severity(val label: String) {
    Info("info"),
    Warning("warnings"),
    Error("errors");

    val icon: Painter
        @Composable
        get() = when (this) {
            Info -> getIcon(Icon.SeverityInfo)
            Warning -> getIcon(Icon.SeverityWarning)
            Error -> getIcon(Icon.SeverityError)
        }

    val color: Color
        @Composable
        get() = when (this) {
            Info -> LocalStyle.current.contentColor
            Warning -> Color.Yellow
            Error -> Color.Red
        }

}
