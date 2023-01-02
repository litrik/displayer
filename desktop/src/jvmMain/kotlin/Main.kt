import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import co.touchlab.kermit.Logger
import com.displayer.app.App
import com.displayer.app.AppState
import com.displayer.app.AppUi
import com.displayer.initKoin
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val koin = initKoin().koin

class Application : CliktCommand() {

    private val url by option(help = "URL of the configuration to show")
    private val size by option(help = "Size of the screen formatted as <width>x<height> (e.g. '1920x108')")
    private val port by option(help = "Admin port", hidden = true)
    private val secret by option(help = "Secret to access the admin port", hidden = true)
    private val openWeatherApiKey by option(help = "OpenWeather API key")

    private val app = koin.get<App>()

    @DelicateCoroutinesApi
    override fun run() {
        val width = size?.substringBefore('x')?.toIntOrNull() ?: 1920
        val height = size?.substringAfterLast('x')?.toIntOrNull() ?: 1080
        val port: Int? = port?.toIntOrNull()
        Logger.d { "Using a window of size ${width}x${height}" }
        GlobalScope.launch {
            openWeatherApiKey?.let {
                app.setOpenWeatherApiKey(it)
            }
            app.loadDisplay(url)
        }
        application {
            val display by app.observeState().collectAsState(AppState.Initializing)
            Window(
                onCloseRequest = ::exitApplication,
                state = WindowState(width = width.dp, height = height.dp, position = WindowPosition(Alignment.BottomEnd)),
                undecorated = true
            ) {
                AppUi(display)
            }
            LaunchedEffect(port) {
                if (port != null && secret != null) {
                    Logger.d("Starting admin endpoint on port $port")
                    embeddedServer(CIO, port = port) {
                        routing {
                            get("/config") {
                                Logger.d("Processing ${call.request.httpMethod.value} ${call.request.path()} request from ${call.request.origin.remoteHost}")
                                val secretParam = call.request.queryParameters["secret"]
                                if (secretParam == secret) {
                                    call.request.queryParameters["url"]?.let { app.loadDisplay(it) }
                                    call.request.queryParameters["open-weather-api-key"]?.let { app.setOpenWeatherApiKey(it) }
                                    call.respond(HttpStatusCode.OK, "OK")
                                } else {
                                    call.respond(HttpStatusCode.Unauthorized, "ERROR")
                                }
                            }
                            post("/config/display") {
                                Logger.d("Processing ${call.request.httpMethod.value} ${call.request.path()} request from ${call.request.origin.remoteHost}")
                                call.receiveMultipart().forEachPart { part ->
                                    if (part is PartData.FileItem) {
                                        app.loadDisplay(part.streamProvider())
                                    }
                                    part.dispose
                                }
                            }
                        }
                    }.start(wait = false)
                }
            }
        }
    }
}

fun main(args: Array<String>) = Application().main(args)
