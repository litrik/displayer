package com.displayer.display.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.displayer.display.item.Item
import com.displayer.ui.Direction
import com.displayer.ui.LocalDimensions
import com.displayer.ui.LocalDirection
import com.displayer.ui.LocalStyle
import com.displayer.ui.LocalTextAlign
import com.displayer.ui.StyledContent
import kotlinx.collections.immutable.ImmutableList

sealed class Container(
    val direction: Direction = Direction.None,
    val textAlign: TextAlign = TextAlign.Center,
) {
    abstract val padding: com.displayer.display.Padding
    abstract val style: com.displayer.display.Style?
    abstract val items: ImmutableList<Item>
}

@Composable
fun ContainerUi(container: Container, modifier: Modifier) {
    StyledContent(container.style) {
        CompositionLocalProvider(
            LocalDirection provides container.direction,
            LocalTextAlign provides container.textAlign,
        ) {
            val paddingHorizontal = LocalDimensions.current.baseUnit * container.padding.horizontal
            val paddingVertical = LocalDimensions.current.baseUnit * container.padding.vertical
            Box(modifier = modifier.background(LocalStyle.current.backgroundColor).fillMaxSize().padding(horizontal = paddingHorizontal, vertical = paddingVertical)) {
                when (container) {
                    is Empty -> EmptyUi()
                    is StackLayout -> StackLayoutUi(container)
                    is ColumnLayout -> ColumnLayoutUi(container)
                    is RowLayout -> RowLayoutUi(container)
                }
            }
        }
    }
}
