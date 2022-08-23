package pirale.sharedlogger.publisher

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDateTime
import java.util.Timer
import kotlin.system.exitProcess

object App {
    private fun startPing() {

        fun msg() = LogRecord(2, "salve, son un log", mapOf("id" to "ciao"), LocalDateTime.now())

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