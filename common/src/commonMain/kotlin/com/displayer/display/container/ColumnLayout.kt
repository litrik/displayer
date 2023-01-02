package com.displayer.display.container

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.display.item.Item
import com.displayer.display.item.ItemUi
import com.displayer.toPx
import com.displayer.ui.Direction
import com.displayer.ui.LocalDimensions
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

data class ColumnLayout(
    override val padding: Padding,
    override val style: Style? = null,
    override val items: ImmutableList<Item>,
    val spacing: Float? = null,
    val scrollSpeedSeconds: Float,
) : Container(
    direction = Direction.Vertical,
) {

    companion object {
        val DEFAULT_PADDING_SCROLLING: Padding = Padding(1f, 0f)
        val DEFAULT_PADDING_STATIC: Padding = Padding(1f, 1f)
        const val DEFAULT_SCROLL_SPEED: Float = 0f
    }
}

@Composable
fun ColumnLayoutUi(container: ColumnLayout) {
    with(container) {
        if (scrollSpeedSeconds > 0f) {
            val scrollState = rememberScrollState()
            val scrollDistance = LocalDimensions.current.screenHeight
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.baseUnit * (container.spacing ?: container.padding.horizontal)),
            ) {
                Spacer(Modifier.height(scrollDistance))
                items.forEach { ItemUi(it) }
                Spacer(Modifier.height(scrollDistance))
            }

            val durationFactor: Int = (scrollState.maxValue / scrollDistance.toPx()).toInt()
            LaunchedEffect(scrollState.maxValue) {
                delay(1000)
                scrollState.scrollTo(0)
                scrollState.animateScrollTo(
                    scrollState.maxValue, infiniteRepeatable(
                        animation = tween((scrollSpeedSeconds * 1000 * durationFactor).toInt(), easing = LinearEasing), repeatMode = RepeatMode.Restart
                    )
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.baseUnit * (container.spacing ?: container.padding.horizontal)),
            ) {
                items.forEach { ItemUi(it) }
            }
        }
    }
}
