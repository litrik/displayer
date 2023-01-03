package com.displayer.admin

import co.touchlab.kermit.Logger
import com.displayer.config.AdminParameters
import com.displayer.config.ConfigRepo
import com.displayer.display.DisplayRepo
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AdminServer(
    private val configRepo: ConfigRepo,
    private val displayRepo: DisplayRepo,
) {

    private var state = MutableStateFlow<AdminState>(AdminState.Stopped)
    private var adminEngine: ApplicationEngine? = null

    init {
        GlobalScope.launch {
            configRepo.observeConfig().map { it.adminParameters }.distinctUntilChanged().collect {
                if (it == null) {
                    stop()
                } else {
                    start(it)
                }
            }
        }
    }

    fun observeState(): Flow<AdminState> = state

    private fun stop() {
        Logger.i("Stopping existing admin server")
        adminEngine?.stop()
        state.value = AdminState.Stopped
    }

    private fun start(params: AdminParameters) {
        adminEngine?.let { stop() }
        Logger.i("Starting admin server on port ${params.port}")
        adminEngine = embeddedServer(CIO, port = params.port) {
            routing {
                get("/config") {
                    Logger.d("Processing ${call.request.httpMethod.value} ${call.request.path()} request from ${call.request.origin.remoteHost}")
                    val secretParam = call.request.queryParameters["secret"]
                    if (secretParam == params.secret) {
                        call.request.queryParameters["url"]?.let { displayRepo.loadDisplay(it) }
                        call.request.queryParameters["open-weather-api-key"]?.let { configRepo.setOpenWeatherApiKey(it) }
                        if (call.request.queryParameters.contains("kill-server")) {
                            configRepo.setAdminParameters(null)
                        }
                        call.respond(HttpStatusCode.OK, "OK")
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "ERROR")
                    }
                }
                post("/config/display") {
                    Logger.d("Processing ${call.request.httpMethod.value} ${call.request.path()} request from ${call.request.origin.remoteHost}")
                    call.receiveMultipart().forEachPart { part ->
                        if (part is PartData.FileItem) {
                            displayRepo.loadDisplay(part.streamProvider())
                        }
                        part.dispose
                    }
                }
            }
        }.start(wait = false)
        state.value = AdminState.Running(params.port)
    }

}