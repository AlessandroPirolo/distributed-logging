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

    private fun begin() {
        /*consoleMessage("press G to start pinging or Q to quit"){
            when (it) {
                "g"  -> startPing()
                "q"  -> quit()
            }
        }*/
        startPing()

    }

    private fun startPing() {

        val msg = LogRecord(2, "salve, son un log", mapOf("id" to "ciao"), getNow())

        val sharedLogger: SharedLogger = SharedLoggerFactory().create()
        GlobalScope.launch {
            while (isActive) {
                sharedLogger.put(msg)
                delay(50)
            }
        }







        //delay(2000L)
    }
    private fun getNow(): LocalDateTime {
        return LocalDateTime.now()
    }


    /*private fun quit() {
        println("goodbye")
        exitProcess(0)
    }

    private tailrec fun consoleMessage(msg: String, f: (String?) -> Unit) {
        println(msg)
        f(readLine())
        return consoleMessage(msg, f)
    }*/

    @JvmStatic
    fun main(args: Array<String>) {
        begin()
    }
}