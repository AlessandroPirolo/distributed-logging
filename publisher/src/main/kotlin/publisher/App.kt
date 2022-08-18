package publisher


import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.*
import publisher.impl.SharedLoggerFactory
import publisher.serialization.LogRecordPBSerializer
import java.time.LocalDateTime
import kotlin.system.exitProcess

object App {



    fun begin() {
        consoleMessage("press G to start pinging or Q to quit"){
            when (it) {
                "g"  -> startPing()
                "q"  -> quit()
            }
        }

    }

    fun startPing() {
        var client = MqttClient("tcp://127.0.0.1:1883", MqttClient.generateClientId())
        val topic = "ciao"
        val qos = 1

        /** Trying to connect to the broker **/
        val factory = SharedLoggerFactory(client, topic, LogRecordPBSerializer())
        val factory1 = factory.simpleMqttSharedLogger(client, topic, MqttConnectOptions(), LogRecordPBSerializer())

        /** Subscription to topic **/

        //var logQueue: SynchronousQueue<LogRecord> = SynchronousQueue()

        GlobalScope.launch {
            while (isActive) {
                /** Trying to publish a message **/
                val msg: LogRecord = LogRecord(2, "salve, sono un log", mapOf("id" to "ciao"), getNow())
                factory1.put(msg)
                //println("Created log Record")
                //log(msg, logQueue, client, "ciao")

                delay(2000L)
            }
        }.also { job ->
            consoleMessage("press S to stop or Q to quit ") {
                when (it) {
                    "s" -> {
                        job.cancel()
                        begin()
                    }

                    "q" -> {
                        job.cancel()
                        quit()
                    }
                }
            }
        }

    }

    private fun getNow(): LocalDateTime {
        return LocalDateTime.now()
    }

    /*private fun queue(log: LogRecord, queue: SynchronousQueue<LogRecord>): SynchronousQueue<LogRecord> {
        val queueSize = queue.size
        println("Checking queue size")
        if(queueSize > 19)    {
            println("Queue size not ok")
            val numLogToRem = queueSize - 19 + 1
            for (i in 0..numLogToRem) {
                queue.poll()
            }
            val warnLog = LogRecord(3,"Removed $numLogToRem logs", log.tags, getNow())
            queue.put(warnLog)
        }
        else    {
            println("Queue size ok")
            queue.add(log)
            println("add log to Queue")
        }
        return queue
    }*/

    /*private fun log(log: LogRecord, logQueue: SynchronousQueue<LogRecord>, client: MqttClient, topic: String): Unit {
        queue(log, logQueue)
        println("Queued log to the queue")
        GlobalScope.launch {
            while (isActive) {
                /** Trying to publish a message **/
                try {
                    println("Checking if queue isn't empty")
                    while (logQueue.isNotEmpty()) {
                        val message = MqttMessage()
                        message.payload = logQueue.poll().getLog().toByteArray()
                        client.publish(topic, message.payload, 1, true)
                        println("*** ${log.toString()} published to $topic ***")
                    }
                } catch (e: MqttException) {
                    println("Error Publishing to $topic: " + e.message)
                    e.printStackTrace()
                }
                delay(2000L)
            }
        }
    }*/

    fun quit() {
        println("goodbye")
        exitProcess(0)
    }

    tailrec fun consoleMessage(msg: String, f: (String?) -> Unit) {
        println(msg)
        f(readLine())
        return consoleMessage(msg, f)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        begin()
    }
}