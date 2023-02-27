package com.displayer.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.intl.Locale
import com.displayer.ui.Icon
import kotlinx.datetime.Instant
import okio.FileSystem
import okio.Path

@Composable
expect fun getIcon(icon: Icon): Painter

expect fun formatTime(instant: Instant, locale: Locale): String

expect fun getDefaultCountry() : String

expect fun getDefaultLanguage() : String

expect fun getFilesystem() : FileSystem

expect fun getDisplayCachePath() : Path