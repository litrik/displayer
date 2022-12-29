package com.displayer.display.item

import androidx.compose.runtime.Composable

data class UnknownItem(
    override val style: com.displayer.display.Style? = null,
    override val padding: com.displayer.display.Padding,
) : Item()

@Composable
fun UnknownItemUi() {
}
