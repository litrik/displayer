package com.displayer.display.container

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.displayer.display.item.Item
import com.displayer.display.item.ItemUi
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

data class StackLayout(
    override val padding: com.displayer.display.Padding,
    override val style: com.displayer.display.Style? = null,
    override val items: ImmutableList<Item>,
    val autoAdvanceInSeconds: Int,
) : Container() {

    companion object {
        const val DEFAULT_AUTO_ADVANCE_IN_SECONDS = 30
        val DEFAULT_PADDING: com.displayer.display.Padding = com.displayer.display.Padding(0f, 0f)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StackLayoutUi(container: StackLayout) {
    with(container) {
        val currentItem = remember { mutableStateOf(0) }
        if (items.isNotEmpty()) {
            LaunchedEffect(true) {
                while (true) {
                    delay(autoAdvanceInSeconds * 1000L)
                    currentItem.value = (currentItem.value + 1) % items.size
                }
            }
        }
        val animationDuration = 500
        AnimatedContent(
            targetState = currentItem.value,
            transitionSpec = { fadeIn(tween(animationDuration)) with fadeOut(tween(animationDuration)) }) { targetState ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                items.getOrNull(targetState.coerceAtMost((items.size - 1).coerceAtLeast(0)))?.run {
                    ItemUi(this)
                }
            }
        }
    }
}
