package com.displayer.display.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.displayer.toSp
import com.displayer.ui.Direction
import com.displayer.ui.LocalDirection
import com.displayer.ui.LocalStyle
import com.displayer.ui.LocalTextAlign

data class TextItem(
    val text: String,
    override val style: com.displayer.display.Style? = null,
    override val padding: com.displayer.display.Padding = com.displayer.display.Padding(),
) : Item()

@Composable
fun TextItemUi(item: TextItem) {
    AutoFitText(text = item.text)
}

@Composable
fun AutoFitText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
) {
    val actualStyle = style.copy(
        color = LocalStyle.current.contentColor,
        textAlign = LocalTextAlign.current,
// FIXME: Disable includeFontPadding. See https://medium.com/androiddevelopers/fixing-font-padding-in-compose-text-768cd232425b
//        platformStyle = PlatformTextStyle(
//            includeFontPadding = false
//        ),
    )
    when (LocalDirection.current) {
        Direction.None -> UnscaledText(text, actualStyle, modifier.fillMaxSize().background(LocalStyle.current.backgroundColor))
        Direction.Horizontal -> AutoFitTextVertically(text, actualStyle, modifier.fillMaxHeight().background(LocalStyle.current.backgroundColor))
        Direction.Vertical -> AutoFitTextHorizontally(text, actualStyle, modifier.fillMaxWidth().background(LocalStyle.current.backgroundColor))
    }
}

@Composable
fun UnscaledText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = style.copy(
                fontSize = maxHeight.toSp().times(0.1f)
            ),
        )
    }
}

@Composable
fun AutoFitTextVertically(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        BasicText(
            text = text,
            modifier = Modifier.fillMaxHeight(),
            style = style.copy(
                fontSize = maxHeight.toSp().times(0.65f)
            ),
            maxLines = 1,
        )
    }
}

@Composable
fun AutoFitTextHorizontally(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        var shrunkFontSize = 400.sp
        val calculateIntrinsics = @Composable {
            ParagraphIntrinsics(
                text = text,
                style = style.copy(fontSize = shrunkFontSize),
                density = LocalDensity.current,
                fontFamilyResolver = LocalFontFamilyResolver.current
            )
        }

        var intrinsics = calculateIntrinsics()
        with(LocalDensity.current) {
            while (intrinsics.maxIntrinsicWidth > maxWidth.toPx()) {
                shrunkFontSize *= 0.9f
                intrinsics = calculateIntrinsics()
            }
        }

        BasicText(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = style.copy(fontSize = shrunkFontSize),
            maxLines = 1,
        )
    }
}
