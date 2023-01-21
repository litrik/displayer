package com.displayer

import com.displayer.admin.AdminServer
import com.displayer.admin.HttpAdminServer
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.module

val jvmModule = module {
    single {
        HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(get(), contentType = ContentType.Any)
            }
        }
    }
    single<AdminServer>(createdAtStart = true) { HttpAdminServer(get(), get())}
}
