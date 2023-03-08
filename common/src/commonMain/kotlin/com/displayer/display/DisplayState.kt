package com.displayer.display

import com.displayer.display.parser.Message
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

sealed class DisplayState(
    open val instant: Instant,
    open val url: String? = null,
    open val messages: ImmutableList<Message> = persistentListOf()
) {
    class NoDisplay : DisplayState(Clock.System.now())

    data class Success(
        override val url: String? = null,
        override val messages: ImmutableList<Message>,
        val display: Display,
    ) : DisplayState(Clock.System.now(), url, messages)

    data class Failure(
        override val url: String?,
        override val messages: ImmutableList<Message>,
    ) : DisplayState(Clock.System.now(), url, messages)

}
