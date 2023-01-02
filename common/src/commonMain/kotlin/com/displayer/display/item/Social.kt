package com.displayer.display.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.display.parser.SocialApp
import com.displayer.isLight
import com.displayer.platform.getIcon
import com.displayer.ui.Direction
import com.displayer.ui.Icon
import com.displayer.ui.LocalDimensions
import com.displayer.ui.LocalDirection
import com.displayer.ui.LocalStyle
import com.displayer.ui.LocalTextAlign

data class SocialItem(
    val app: SocialApp,
    val account: String,
    val text: String?,
    override val style: Style? = null,
    override val padding: Padding = Padding(),
) : Item()

@Composable
fun SocialItemUi(item: SocialItem) {
    when (LocalDirection.current) {
        Direction.None -> SocialItemOther(item)
        Direction.Horizontal -> SocialItemRow(item)
        Direction.Vertical -> SocialItemColumn(item)
    }
}

@Composable
fun SocialItemOther(item: SocialItem) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        val actualStyle = TextStyle.Default.copy(
            color = LocalStyle.current.contentColor,
            textAlign = LocalTextAlign.current,
        )
        Spacer(modifier = Modifier.weight(2f))
        item.text?.run {
            UnscaledText(this, actualStyle)
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.weight(6f).aspectRatio(2f),
        ) {
            Image(
                getIcon(item.app.asIcon()),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        UnscaledText(item.account, actualStyle)
        Spacer(modifier = Modifier.weight(2f))
    }
}

@Composable
fun SocialItemRow(item: SocialItem) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(LocalDimensions.current.baseUnit),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            getIcon(item.app.asIcon()),
            contentDescription = null,
            modifier = Modifier.fillMaxHeight(),
            contentScale = ContentScale.FillHeight
        )
        AutoFitText(listOfNotNull(item.text, item.account).joinToString(": "))
    }
}

@Composable
fun SocialItemColumn(item: SocialItem) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalDimensions.current.baseUnit),
    ) {
        Image(
            getIcon(item.app.asIcon()),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(0.8f),
            contentScale = ContentScale.FillWidth
        )
        AutoFitText(item.account)
    }
}

@Composable
fun SocialApp.asIcon() = when (this) {
    SocialApp.Facebook -> Icon.Facebook
    SocialApp.Instagram -> Icon.Instagram
    SocialApp.Tiktok -> Icon.TikTok
    SocialApp.YouTube -> if (LocalStyle.current.backgroundColor.isLight()) Icon.YouTubeLight else Icon.YouTubeDark
    SocialApp.Snapchat -> Icon.Snapchat
}
