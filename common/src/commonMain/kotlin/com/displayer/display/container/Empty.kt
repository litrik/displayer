package com.displayer.display.container

import androidx.compose.runtime.Composable
import com.displayer.display.item.Item
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Empty(
    override val padding: com.displayer.display.Padding = com.displayer.display.Padding(0f, 0f),
    override val style: com.displayer.display.Style? = null,
    override val items: ImmutableList<Item> = persistentListOf(),
) : Container()

@Composable
fun EmptyUi() {
}
