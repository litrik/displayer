package com.displayer

import com.displayer.admin.AdminServer
import com.displayer.admin.NoopAdminServer
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.dsl.module

val jsModule = module {
    single {
        HttpClient(Js) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(get(), contentType = ContentType.Any)
            }
        }
    }
    single<AdminServer> { NoopAdminServer() }
}
