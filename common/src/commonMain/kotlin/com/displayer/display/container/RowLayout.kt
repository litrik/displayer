package com.displayer.display.container

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.CenterVertically
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

data class RowLayout(
    override val padding: Padding,
    override val style: Style? = null,
    override val items: ImmutableList<Item>,
    val spacing: Float? = null,
    val scrollSpeedSeconds: Float,
) : Container(
    direction = Direction.Horizontal,
) {

    companion object {
        val DEFAULT_PADDING_SCROLLING: Padding = Padding(0f, 1f)
        val DEFAULT_PADDING_STATIC: Padding = Padding(1f, 1f)
        const val DEFAULT_SCROLL_SPEED: Float = 8f
    }
}

@Composable
fun RowLayoutUi(container: RowLayout) {
    with(container) {
        if (scrollSpeedSeconds > 0f) {
            val scrollState = rememberScrollState()
            val scrollDistance = LocalDimensions.current.screenWidth
            Row(
                modifier = Modifier.fillMaxSize().horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.baseUnit * (container.spacing ?: container.padding.vertical)),
            ) {
                Spacer(Modifier.width(scrollDistance))
                items.forEach { ItemUi(it) }
                Spacer(Modifier.width(scrollDistance))
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
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.baseUnit * (container.spacing ?: container.padding.vertical)),
                verticalAlignment = CenterVertically,
            ) {
                items.forEach { ItemUi(it) }
            }
        }
    }
}
