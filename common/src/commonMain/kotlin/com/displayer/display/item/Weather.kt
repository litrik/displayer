package com.displayer.display.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.displayer.platform.formatTime
import com.displayer.platform.getIcon
import com.displayer.toSp
import com.displayer.ui.Direction
import com.displayer.ui.Icon
import com.displayer.ui.LocalDimensions
import com.displayer.ui.LocalDirection
import com.displayer.ui.LocalLocale
import com.displayer.ui.LocalStyle
import com.displayer.ui.Text
import com.displayer.weather.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.roundToInt

data class Weather(
    val observeCurrentWeather: () -> Flow<List<WeatherData>>,
    override val style: com.displayer.display.Style? = null,
    override val padding: com.displayer.display.Padding,
) : Item()

@Composable
fun WeatherUi(item: Weather) {
    val weatherData by item.observeCurrentWeather().collectAsState(null)
    when (LocalDirection.current) {
        Direction.None -> weatherData?.run { WeatherItemOther(this) }
        Direction.Horizontal -> weatherData?.firstOrNull()?.run { WeatherItemRow(this) }
        Direction.Vertical -> weatherData?.firstOrNull()?.run { WeatherItemColumn(this, Arrangement.Top) }
    }
}

@Composable
fun WeatherItemOther(data: List<WeatherData>) {
    CompositionLocalProvider(
        LocalDirection provides Direction.Vertical,
    ) {
        val locale = LocalLocale.current
        Row {
            Spacer(modifier = Modifier.weight(10f))
            data.getOrNull(0)?.let { data ->
                Box(modifier = Modifier.weight(25f)) {
                    WeatherItemColumn(data, title = formatTime(Clock.System.now(), locale))
                }
            }
            Spacer(modifier = Modifier.weight(15f))
            data.getOrNull(1)?.let { data ->
                Box(modifier = Modifier.weight(25f)) {
                    WeatherItemColumn(data, title = formatTime(Instant.fromEpochSeconds(data.timeStamp), locale))
                }
            }
            Spacer(modifier = Modifier.weight(15f))
            data.getOrNull(2)?.let { data ->
                Box(modifier = Modifier.weight(25f)) {
                    WeatherItemColumn(data, title = formatTime(Instant.fromEpochSeconds(data.timeStamp), locale))
                }
            }
            Spacer(modifier = Modifier.weight(10f))
        }
    }
}

@Composable
fun WeatherItemRow(data: WeatherData) {
    Row(
        horizontalArrangement = spacedBy(LocalDimensions.current.baseUnit),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AutoFitText(text = "%d°".format(data.temperature.roundToInt()))
        Image(
            painter = data.icon,
            contentDescription = null,
            modifier = Modifier.fillMaxHeight(0.8f),
            colorFilter = ColorFilter.tint(LocalStyle.current.contentColor),
            contentScale = ContentScale.FillHeight,
        )
        data.description?.run {
            AutoFitText(text = this)
        }
        Image(
            painter = getIcon(Icon.WindDirection),
            contentDescription = null,
            modifier = Modifier.fillMaxHeight(0.7f).rotate(180f + (data.windDegrees)),
            colorFilter = ColorFilter.tint(LocalStyle.current.contentColor),
            contentScale = ContentScale.FillHeight,
        )
        AutoFitText(text = data.formatWindSpeed())
    }
}

@Composable
fun WeatherItemColumn(data: WeatherData, verticalArrangement: Arrangement.Vertical = spacedBy(LocalDimensions.current.baseUnit), title: String? = null) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement,
    ) {
        val textStyle = TextStyle.Default.copy(fontSize = (LocalDimensions.current.fontSize * 0.8f).toSp())
        val textStyleLarge = TextStyle.Default.copy(fontSize = (LocalDimensions.current.fontSize * 1.8f).toSp())
        title?.run {
            Text(this, style = TextStyle.Default.copy(fontWeight = FontWeight.Light, fontSize = (LocalDimensions.current.fontSize * 1.4f).toSp()))
        }
        Text(text = data.formatTemperature(), style = textStyleLarge)
        Image(
            painter = data.icon,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.5f),
            colorFilter = ColorFilter.tint(LocalStyle.current.contentColor),
            contentScale = ContentScale.FillWidth,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = getIcon(Icon.WindDirection),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.2f).rotate(180f + (data.windDegrees)),
                colorFilter = ColorFilter.tint(LocalStyle.current.contentColor),
                contentScale = ContentScale.FillHeight,
            )
            Text(text = data.formatWindSpeed(), style = textStyle.copy(fontWeight = FontWeight.Light))
        }
    }
}
