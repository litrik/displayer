package com.displayer.display

import Strings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import com.displayer.toSp
import com.displayer.ui.LocalDimensions
import com.displayer.ui.Message
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import io.ktor.http.*

@Composable
fun ErrorUi(displayFailed: DisplayState.Failure) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        BasicText(
            text = Strings.errorTitle.toString(),
            modifier = Modifier.fillMaxWidth().background(Color.Red).padding(horizontal = LocalDimensions.current.baseUnit * 2, vertical = LocalDimensions.current.baseUnit),
            style = TextStyle.Default.copy(color = Color.White, fontSize = LocalDimensions.current.fontSize.toSp())
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(1f, true).fillMaxHeight().padding(LocalDimensions.current.baseUnit * 2).verticalScroll(rememberScrollState()),
                verticalArrangement = spacedBy(LocalDimensions.current.baseUnit * 2)
            ) {
                val textStyle = TextStyle.Default.copy(fontSize = LocalDimensions.current.fontSize.toSp())
                displayFailed.messages.onEach {
                    Message(it)
                }
            }
            Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(0.2f).padding(LocalDimensions.current.baseUnit),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    BasicText(text = "Config URL")
                    KamelImage(
                        lazyPainterResource(data = "https://quickchart.io/qr?size=300&text=${displayFailed.url.encodeURLParameter()}"),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().background(Color.Yellow),
                        contentScale = ContentScale.FillWidth,
                    )
                }
            }
        }
    }
}
