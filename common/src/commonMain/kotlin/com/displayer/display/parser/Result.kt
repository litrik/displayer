package com.displayer.display.parser

data class Result<T>(
    val data: T,
    val messages: List<Message>
) {
    fun andReport(messages: MutableList<Message>): T {
        messages.addAll(this.messages)
        return data
    }
}
