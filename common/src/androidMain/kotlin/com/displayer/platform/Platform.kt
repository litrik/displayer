package com.displayer.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import com.displayer.R
import com.displayer.ui.Icon
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.*

@Composable
actual fun getIcon(icon: Icon): Painter = painterResource(
    when (icon) {
        Icon.Empty -> 0
        Icon.Logo -> R.drawable.logo_white
        Icon.SeverityInfo -> R.drawable.severity_info
        Icon.SeverityWarning -> R.drawable.severity_warning
        Icon.SeverityError -> R.drawable.severity_error
        Icon.WindDirection -> R.drawable.wind_direction
        Icon.WeatherSunny -> R.drawable.weather_sunny
        Icon.WeatherPartlyCloudy -> R.drawable.weather_partly_cloudy
        Icon.WeatherCloudy -> R.drawable.weather_cloudy
        Icon.WeatherRainy -> R.drawable.weather_rainy
        Icon.WeatherThunderstorm -> R.drawable.weather_thunderstorm
        Icon.WeatherCloudySnowing -> R.drawable.weather_cloudy_snowing
        Icon.WeatherFog -> R.drawable.weather_fog
        Icon.Facebook -> R.drawable.social_facebook
        Icon.Instagram -> R.drawable.social_instagram
        Icon.TikTok -> R.drawable.social_tiktok
        Icon.YouTubeDark -> R.drawable.social_youtube_dark
        Icon.YouTubeLight -> R.drawable.social_youtube_light
        Icon.Snapchat -> R.drawable.social_snapchat
    }
)

actual fun formatTime(instant: Instant, locale: Locale): String {
    val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, java.util.Locale.forLanguageTag(locale.toLanguageTag()))
    return df.format(Date(instant.toEpochMilliseconds()))
}

actual fun getDefaultLanguage() = Locale.current.language

actual fun getDefaultCountry() = Locale.current.region
