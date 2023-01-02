package com.displayer.display.container

import androidx.compose.runtime.Composable
import com.displayer.display.Padding
import com.displayer.display.Style
import com.displayer.display.item.Item
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Empty(
    override val padding: Padding = Padding(0f, 0f),
    override val style: Style? = null,
    override val items: ImmutableList<Item> = persistentListOf(),
) : Container()

@Composable
fun EmptyUi() {
}
