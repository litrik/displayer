package com.displayer

import com.displayer.admin.AdminServer
import com.displayer.app.App
import com.displayer.config.ConfigRepo
import com.displayer.config.ParametersRepo
import com.displayer.display.DisplayRepo
import com.displayer.display.parser.ContainerDto
import com.displayer.display.parser.ItemDto
import com.displayer.display.parser.StackDto
import com.displayer.display.parser.UnknownDto
import com.displayer.weather.WeatherRepo
import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(coreModule)
    }

val coreModule = module {
    single {
        HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(get(), contentType = ContentType.Any)
            }
        }
    }
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                polymorphic(ContainerDto::class) {
                    defaultDeserializer { StackDto.serializer() }
                }
                polymorphic(ItemDto::class) {
                    defaultDeserializer { UnknownDto.serializer() }
                }
            }
        }
    }
    single { Settings() }
    single { ParametersRepo() }
    single { ConfigRepo(get(), get()) }
    single { DisplayRepo(get(), get(), get(), get(), get()) }
    single { WeatherRepo(get(), get(), get()) }
    single { App(get(), get(), get(), get()) }
    single(createdAtStart = true) { AdminServer(get(), get()) }
}
