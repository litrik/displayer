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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private val koin = initKoin().koin

class Application : CliktCommand() {

    private val url by option(help = "URL of the configuration to show")
    private val size by option(help = "Size of the screen formatted as <width>x<height> (e.g. '1920x108')")
    private val adminPort by option(help = "Admin port", hidden = true)
    private val adminSecret by option(help = "Secret to access the admin port", hidden = true)
    private val openWeatherApiKey by option(help = "OpenWeather API key")

    private val app = koin.get<App>()

    @DelicateCoroutinesApi
    override fun run() {
        val width = size?.substringBefore('x')?.toIntOrNull() ?: 1920
        val height = size?.substringAfterLast('x')?.toIntOrNull() ?: 1080
        Logger.d { "Using a window of size ${width}x${height}" }
        app.setAdminParameters(adminPort?.toIntOrNull(), adminSecret)
        openWeatherApiKey?.let {
            app.setOpenWeatherApiKey(it)
        }
        GlobalScope.launch {
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
        }
    }
}

fun main(args: Array<String>) = Application().main(args)
