package com.displayer.display

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.displayer.display.container.ContainerUi
import com.displayer.ui.LocalDimensions
import com.displayer.ui.LocalLocale
import com.displayer.ui.LocalStyle
import com.displayer.ui.StyledContent

@Composable
fun MainUi(display: com.displayer.display.Display) {
    with(display) {
        CompositionLocalProvider(LocalLocale provides (display.locale)) {
            StyledContent(style) {
                Column(
                    modifier = Modifier.fillMaxSize().background(LocalStyle.current.backgroundColor)
                ) {
                    val bottomHeight = LocalDimensions.current.baseUnit * display.bottomHeight
                    Row(Modifier.height(LocalDimensions.current.screenHeight - bottomHeight)) {
                        if (bottomHeight.value > 0f) {
                            ContainerUi(left, Modifier.weight(1f, true).fillMaxSize())
                        }
                        ContainerUi(center, Modifier.fillMaxHeight().aspectRatio(LocalDimensions.current.screenAspectRatio))
                    }
                    if (bottomHeight.value > 0f) {
                        ContainerUi(bottom, Modifier.height(bottomHeight).fillMaxWidth())
                    }
                }
            }
        }
    }
}
