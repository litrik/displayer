package com.displayer.display.item

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.ui.Direction
import com.displayer.ui.LocalDirection
import io.kamel.core.ExperimentalKamelApi
import io.kamel.core.Resource
import io.kamel.image.KamelImageBox
import io.kamel.image.lazyPainterResource

data class Image(
    val url: String,
    override val style: Style? = null,
    override val padding: Padding = Padding(),
    val scale: ContentScale,
) : Item()


@Composable
fun ImageUi(item: Image) {
    BetterKamelImage(
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

@OptIn(ExperimentalKamelApi::class)
@Composable
public fun BetterKamelImage(
    resource: Resource<Painter>,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    onLoading: @Composable (BoxScope.(Float) -> Unit)? = null,
    onFailure: @Composable (BoxScope.(Throwable) -> Unit)? = null,
    contentAlignment: Alignment = Alignment.Center,
    animationSpec: FiniteAnimationSpec<Float>? = null,
) {
    val onSuccess: @Composable (BoxScope.(Painter) -> Unit) = { painter ->
        Image(
            painter,
            contentDescription,
            modifier,
            alignment,
            contentScale,
            alpha,
            colorFilter
        )
    }
    KamelImageBox(
        resource,
        modifier,
        contentAlignment,
        animationSpec,
        onLoading,
        onFailure,
        onSuccess,
    )
}