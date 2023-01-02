package com.displayer.display

import com.displayer.display.parser.Message
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed class DisplayState(
    open val url: String? = null,
    open val messages: ImmutableList<Message> = persistentListOf()
) {
    object NoDisplay : DisplayState()

    data class Success(
        override val url: String? = null,
        override val messages: ImmutableList<Message>,
        val display: Display,
    ) : DisplayState(url, messages)

    data class Failure(
        override val url: String?,
        override val messages: ImmutableList<Message>,
    ) : DisplayState(url, messages)

}
