package publisher

import com.google.protobuf.Timestamp
import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.*
import publisher.protobuf.LogKt.tag
import publisher.protobuf.LogOuterClass.Log
import publisher.protobuf.LogOuterClass.Log.LogLevel
import publisher.protobuf.LogOuterClass.Log.Tag
import publisher.protobuf.log
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.system.exitProcess

object App {

    data class LogRecord(val logLevel: Int, val msg: String, val tags: Map<String, String> = mapOf()) {

        private val tmpTags: List<Tag> = tags.map {
            tag {
                this.key = it.key
                this.value = it.value
            }
        }

        private val tmpLogLevel = when(logLevel) {
            0 -> LogLevel.TRACE
            1 -> LogLevel.DEBUG
            2 -> LogLevel.INFORMATION
            3 -> LogLevel.WARNING
            4 -> LogLevel.ERROR
            5 -> LogLevel.CRITICAL
            6 -> LogLevel.NONE
            else -> LogLevel.INFORMATION
        }

        private val now: Instant = Instant.now()

        private val log = log {
            this.tag += tmpTags
            this.logLevel = tmpLogLevel
            this.msg = msg
            this.timeStamp = Timestamp.newBuilder().setSeconds( now.epochSecond).setNanos(now.nano).build()
        }


        override fun toString(): String {
            val date = Instant
                .ofEpochSecond( this.log.timeStamp.seconds, this.log.timeStamp.nanos.toLong())
                .atZone( ZoneId.of( "Europe/Rome" ) )
                .toLocalDateTime()

            return this.log.logLevel.toString() +
                    " " + date.toString()+
                    " " + this.log.msg +
                    " " + this.log.tagList.toString()
        }

        fun toByteArray(): ByteArray {
            return this.log.toByteArray()
        }
    }

    private fun parseFrom(array: ByteArray): LogRecord? {
        val log: Log = Log.parseFrom(array)
        return LogRecord(log.logLevel.number, log.msg, log.tagList.associate { it.key to it.value })
    }

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
        try {
            val option = MqttConnectOptions()
            option.isCleanSession = true

            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    println("conn Comple")
                    println("Connected to: $serverURI")
                }

                override fun connectionLost(cause: Throwable) {
                    println("The Connection was lost.")
                }


                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    println("Incoming message from $topic: ${parseFrom(message.payload).toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {

                }
            })
            client.connect(option)
        } catch (e: MqttException) {
            println("e")
            e.printStackTrace()
        }


        /** Subscription to topic **/
        client.subscribe(topic, qos)

        GlobalScope.launch {

            while (isActive) {
                /** Trying to publish a message **/
                try {
                    val msg: LogRecord = LogRecord(2, "ciao", mapOf("id" to "ciao") )
                    val message = MqttMessage()
                    message.payload = msg.toByteArray()
                    client.publish(topic, message.payload, 1, true)
                    println("*** $msg published to $topic")
                } catch (e: MqttException) {
                    println("Error Publishing to $topic: " + e.message)
                    e.printStackTrace()
                }
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

    fun quit() {
        println("goodbye")
        exitProcess(0)
    }

    tailrec fun consoleMessage(msg: String, f: (String?) -> Unit) {
        println(msg)
        f(readLine())
        return consoleMessage(msg, f)
    }

    fun generateTag(_key: String, _value: String): Tag {
        return tag {
            this.key = _key
            this.value = _value
        }
    }




    @JvmStatic
    fun main(args: Array<String>) {
        begin()
    }
}