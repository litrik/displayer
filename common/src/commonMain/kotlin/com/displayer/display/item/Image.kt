package com.displayer.display.item

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.ui.Direction
import com.displayer.ui.LocalDirection
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource

data class Image(
    val url: String,
    override val style: Style? = null,
    override val padding: Padding = Padding(),
    val scale: ContentScale,
) : Item()


@Composable
fun ImageUi(item: Image) {
    KamelImage(
        lazyPainterResource(data = item.url),
        contentDescription = null,
        modifier = when (LocalDirection.current) {
            Direction.None -> Modifier.fillMaxSize()
            Direction.Horizontal -> Modifier.fillMaxHeight()
            Direction.Vertical -> Modifier.fillMaxWidth()
        },
        contentScale = when (LocalDirection.current) {
            Direction.None -> item.scale
            Direction.Horizontal -> ContentScale.FillHeight
            Direction.Vertical -> ContentScale.FillWidth
        }
    )
}
