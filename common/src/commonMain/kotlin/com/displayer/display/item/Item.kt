package com.displayer.display.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.ui.LocalDimensions
import com.displayer.ui.StyledContent

sealed class Item {
    abstract val style: Style?
    abstract val padding: Padding
}

@Composable
fun ItemUi(item: Item) {
    StyledContent(item.style) {
        val paddingHorizontal = LocalDimensions.current.baseUnit * item.padding.horizontal
        val paddingVertical = LocalDimensions.current.baseUnit * item.padding.vertical
        Box(modifier = Modifier.padding(horizontal = paddingHorizontal, vertical = paddingVertical)) {
            when (item) {
                is UnknownItem -> UnknownItemUi()
                is Clock -> ClockUi()
                is Image -> ImageUi(item)
                is TextItem -> TextItemUi(item)
                is RandomItem -> RandomItemUi(item)
                is Weather -> WeatherUi(item)
                is DrinkItem -> DrinkItemUi(item)
                is SocialItem -> SocialItemUi(item)
            }
        }
    }
}
