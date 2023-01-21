package com.displayer.admin

import kotlinx.coroutines.flow.Flow

interface AdminServer {

    fun observeState() : Flow<AdminState>
}
