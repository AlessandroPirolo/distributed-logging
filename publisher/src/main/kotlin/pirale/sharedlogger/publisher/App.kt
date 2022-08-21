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

    /*private fun begin() {
        consoleMessage("press G to start pinging or Q to quit"){
            when (it) {
                "g"  -> startPing()
                "q"  -> quit()
            }
        }

    }*/

    /*private fun startPing() {

        val msg = LogRecord(2, "salve, son un log", mapOf("id" to "ciao"), getNow())

        val sharedLogger: SharedLogger = SharedLoggerFactory().create()

        sharedLogger.put(msg)

        delay(2000L)
    }
    private fun getNow(): LocalDateTime {
        return LocalDateTime.now()
    }


    private fun quit() {
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
        //begin()
        val channel = Channel<String>()


        GlobalScope.launch {

            val list = mutableListOf<String>()
            var lastSumbmission = Instant.now()
            var last: String? = null
            channel.receiveAsFlow().combineTransform(ticker(50,0).receiveAsFlow().map { Instant.now() }) { s, t ->

                if (s !== last) {
                    list.add(s)
                    last = s
                }

                val now = Instant.now()
                val emitCondition = list.count() == 10 ||
                        (list.isNotEmpty() && lastSumbmission.isBefore(t.minusMillis(50)))
                if (emitCondition) {
                    emit(list)
                    //println(list)
                    lastSumbmission = Instant.now()
                    list.clear()
                }
            }.onEach { println(it.toString()) }.collect()
        }

        GlobalScope.launch {
            delay(1000)
            (1 .. 100).forEach{channel.send("$it"); delay(5) }
        }


        readLine()
    }
}