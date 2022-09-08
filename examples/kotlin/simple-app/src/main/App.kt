import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDateTime
import java.util.Timer
import kotlin.system.exitProcess
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SharedLogger
import pirale.sharedlogger.publisher.SharedLoggerFactory

object App {
    private fun startPing() {

        fun msg() = LogRecord(2, "Hi, I'm a log", mapOf("id" to "mqttTest"), LocalDateTime.now())

        val sharedLogger: SharedLogger = SharedLoggerFactory().create()
        GlobalScope.launch {
            while (isActive) {
                sharedLogger.put(msg())
                delay(50)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        startPing()
    }
}