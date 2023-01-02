package com.displayer.display.item

import androidx.compose.runtime.Composable
import com.displayer.display.Padding
import com.displayer.display.Style

data class UnknownItem(
    override val style: Style? = null,
    override val padding: Padding,
) : Item()

@Composable
fun UnknownItemUi() {
}
