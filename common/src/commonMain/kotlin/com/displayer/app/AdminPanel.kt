package com.displayer.app

import Strings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.displayer.BuildKonfig
import com.displayer.display.DisplayState
import com.displayer.platform.formatTime
import com.displayer.platform.getIcon
import com.displayer.toSp
import com.displayer.ui.DisplayerPurple
import com.displayer.ui.Icon
import com.displayer.ui.LocalDimensions
import com.displayer.ui.LocalLocale
import com.displayer.ui.Message
import com.displayer.ui.Text
import com.displayer.weather.WeatherState

@Composable
fun AdminPanel(state: AppState) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.5f)
            .background(Color.Black),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(DisplayerPurple).padding(LocalDimensions.current.baseUnit),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = spacedBy(LocalDimensions.current.baseUnit)
        ) {
            Image(getIcon(Icon.Logo), null, modifier = Modifier.size(LocalDimensions.current.baseUnit * 2))
            Text("Displayer")
        }
        Column(
            Modifier.padding(LocalDimensions.current.baseUnit),
            verticalArrangement = spacedBy(LocalDimensions.current.baseUnit)
        ) {

            SectionTitle(Strings.adminSectionApp.toString())

            Text("${Strings.adminLabelVersion}: ${BuildKonfig.APP_VERSON_NAME}")

            if (state is AppState.Ready) {

                SectionTitle(Strings.adminSectionWeather.toString())
                Text("${Strings.adminLabelStatus}: ${getKeyLabel(state.weatherState)}")

                if (state.displayState !is DisplayState.NoDisplay) {

                    SectionTitle(Strings.adminSectionDisplay.toString())
                    state.displayState.url?.run { Text("${Strings.adminLabelUrl}: $this") }
                    Column() {
                        state.displayState.messages.onEach {
                            Message(
                                message = it,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getKeyLabel(state: WeatherState): String = when (state) {
    is WeatherState.ApiKeyInvalid -> Strings.adminWeatherApiKeyInvalid.toString()
    WeatherState.ApiKeyNotSet -> Strings.adminWeatherApiKeyNotSet.toString()
    is WeatherState.Failure -> "${Strings.adminWeatherFailure} (${formatTime(state.instant, LocalLocale.current)})"
    is WeatherState.Success -> "${Strings.adminWeatherSuccess} (${formatTime(state.instant, LocalLocale.current)})"
}

@Composable
fun SectionTitle(text: String) {
    Text(text, style = TextStyle.Default.copy(fontSize = LocalDimensions.current.fontSizeSmall.toSp()))
}

