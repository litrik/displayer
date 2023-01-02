package com.displayer.display.item

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import com.displayer.display.Padding
import com.displayer.display.Style
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.random.Random

data class RandomItem(
    val items: ImmutableList<Item> = persistentListOf(),
    override val style: Style? = null,
    override val padding: Padding = Padding(),
) : Item()

@Composable
fun RandomItemUi(item: RandomItem) {
    if (item.items.isEmpty()) {
        return
    }
    val index = Random.nextInt(0, item.items.size)
    Logger.i { "Picked random item $index" }
    ItemUi(item.items[index])
}
