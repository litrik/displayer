package com.displayer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import com.displayer.display.parser.Message

@Composable
fun Message(message: Message) {
    Row(
        horizontalArrangement = spacedBy(LocalDimensions.current.baseUnit),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = message.severity.icon,
            contentDescription = message.severity.name,
            modifier = Modifier.size(LocalDimensions.current.baseUnit * 2),
            colorFilter = ColorFilter.tint(message.severity.color),
        )
        Text(
            text = message.message,
        )
    }
}
