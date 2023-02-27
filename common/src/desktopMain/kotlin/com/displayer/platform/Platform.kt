package com.displayer.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import com.displayer.ui.Icon
import kotlinx.datetime.Instant
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
actual fun getIcon(icon: Icon): Painter = painterResource(
    when (icon) {
        Icon.Empty -> ""
        Icon.Logo -> "logo_white.svg"
        Icon.SeverityInfo -> "severity_info.svg"
        Icon.SeverityWarning -> "severity_warning.svg"
        Icon.SeverityError -> "severity_error.svg"
        Icon.WindDirection -> "wind_direction.svg"
        Icon.WeatherSunny -> "weather_sunny.svg"
        Icon.WeatherPartlyCloudy -> "weather_partly_cloudy.svg"
        Icon.WeatherCloudy -> "weather_cloudy.svg"
        Icon.WeatherRainy -> "weather_rainy.svg"
        Icon.WeatherThunderstorm -> "weather_thunderstorm.svg"
        Icon.WeatherCloudySnowing -> "weather_cloudy_snowing.svg"
        Icon.WeatherFog -> "weather_fog.svg"
        Icon.Facebook -> "social_facebook.png"
        Icon.Instagram -> "social_instagram.png"
        Icon.TikTok -> "social_tiktok.png"
        Icon.YouTubeDark -> "social_youtube_dark.png"
        Icon.YouTubeLight -> "social_youtube_light.png"
        Icon.Snapchat -> "social_snapchat.png"
    }
)

actual fun formatTime(instant: Instant, locale: Locale): String {
    val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, java.util.Locale.forLanguageTag(locale.toLanguageTag()))
    return df.format(Date(instant.toEpochMilliseconds()))
}

actual fun getDefaultLanguage() = Locale.current.language

actual fun getDefaultCountry() = Locale.current.region

actual fun getFilesystem(): FileSystem = FileSystem.SYSTEM

actual fun getDisplayCachePath(): Path = File(File(".cache").also { it.mkdirs() }, "/display-cache.json").absolutePath.toPath()
