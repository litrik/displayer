package com.displayer.admin

sealed class AdminState {

    object Stopped : AdminState()

    data class Running(
        val port: Int,
    ) : AdminState()

}