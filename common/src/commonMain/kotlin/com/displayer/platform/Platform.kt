package com.displayer.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.intl.Locale
import com.displayer.ui.Icon
import kotlinx.datetime.Instant

@Composable
expect fun getIcon(icon: Icon): Painter

expect fun formatTime(instant: Instant, locale: Locale): String
