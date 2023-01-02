package com.displayer.display.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.ui.LocalDimensions
import com.displayer.ui.LocalStyle
import com.displayer.ui.LocalTextAlign
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

data class DrinkItem(
    val image: String,
    val text: String,
    override val style: Style? = null,
    override val padding: Padding = Padding(),
    val spacing: Float? = DEFAULT_SPACING,
) : Item() {

    companion object {
        const val DEFAULT_SPACING = 1f
    }
}

@Composable
fun DrinkItemUi(item: DrinkItem) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.baseUnit * (item.spacing ?: item.padding.horizontal)),
    ) {
        Box(
            modifier = Modifier.weight(1f),
        ) {
            KamelImage(
                lazyPainterResource(data = item.image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        val actualStyle = TextStyle.Default.copy(
            color = LocalStyle.current.contentColor,
            textAlign = LocalTextAlign.current,
        )
        UnscaledText(item.text, actualStyle)
    }
}
