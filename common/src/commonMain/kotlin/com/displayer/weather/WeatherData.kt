package com.displayer.weather

import Strings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.displayer.display.parser.Units
import com.displayer.platform.getIcon
import com.displayer.ui.Icon
import com.displayer.weather.api.WeatherDataDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class WeatherData(
    val units: Units,
    val temperature: Float,
    val windSpeed: Float,
    val windDegrees: Float,
    val timeStamp: Long,
    val description: String?,
    @SerialName("icon")
    val iconCode: Int?,
) {

    fun formatTemperature() = "%d°".format(temperature.roundToInt())

    fun formatWindSpeed() = when (units) {
        Units.Metric -> "%.0f %s".format(windSpeed * 3.6, Strings.windSpeedMetric)
        Units.Imperial -> "%.0f %s".format(windSpeed, Strings.windSpeedImperial)
    }

    val icon: Painter
        @Composable
        get() = when (iconCode) {
            1 -> getIcon(Icon.WeatherSunny)
            2 -> getIcon(Icon.WeatherPartlyCloudy)
            3 -> getIcon(Icon.WeatherCloudy)
            4 -> getIcon(Icon.WeatherCloudy)
            9 -> getIcon(Icon.WeatherRainy)
            10 -> getIcon(Icon.WeatherRainy)
            11 -> getIcon(Icon.WeatherThunderstorm)
            13 -> getIcon(Icon.WeatherSunny)
            50 -> getIcon(Icon.WeatherFog)
            else -> getIcon(Icon.Empty)
        }

    companion object {

        fun fromDto(dto: WeatherDataDto, units: Units): WeatherData = WeatherData(
            units = units,
            temperature = dto.main.temp,
            windSpeed = dto.wind.speed,
            windDegrees = dto.wind.deg,
            timeStamp = dto.dt,
            description = dto.weather.firstOrNull()?.description,
            iconCode = dto.weather.firstOrNull()?.icon?.substring(0, 2)?.toIntOrNull()
        )
    }

}
