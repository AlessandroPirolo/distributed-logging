package pirale.sharedlogger.publisher.impl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SendChannel
import pirale.sharedlogger.publisher.SharedLogger

class QueuedSharedLogger(private val sender: SendChannel) : SharedLogger {

    /*private var counter = 0

    private fun queue(logRecord: LogRecord, queue: Queue<LogRecord>) {
        if(queue.count() > 20) {
            println("Too much element.")
            for (i in 0..elToDeleteNum) {
                val log = queue.remove()
                if(!log.tags.containsValue("logs removed"))  {
                    counter++
                }
            }
            val warnLog = LogRecord(3, "$counter log records have been lost", mapOf("type" to "logs removed"), LocalDateTime.now())
            queue.add(warnLog)
        }
        println("Queuing...")
        queue.add(logRecord)
    }*/

    override fun put(logRecord: LogRecord) {
        /*GlobalScope.launch {
            delay(1000)
            (1 .. 100).forEach{channel.send("$it"); delay(5) }
        }*/

        // qua bisogna fare channel.send. Devo creare io una coda?

        sender.send()
    }
}