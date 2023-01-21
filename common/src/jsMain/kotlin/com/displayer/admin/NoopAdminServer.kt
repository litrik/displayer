package com.displayer.admin

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class NoopAdminServer : AdminServer {

    override fun observeState(): Flow<AdminState> = flowOf(AdminState.Stopped)

}
