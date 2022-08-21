package pirale.sharedlogger.publisher.mqtt

import org.eclipse.paho.client.mqttv3.*
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.LogRecordSerializer
import java.time.LocalDateTime
import java.util.*

class LogRecordListMqttClient(private val client: IMqttAsyncClient, private val topic: String, private val options: MqttConnectOptions, private val serializer: LogRecordSerializer) {

    init {
        try {
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    println("conn Completed")
                    println("Connected to: $serverURI")
                }

                override fun connectionLost(cause: Throwable) {
                    println("The Connection was lost.")
                    println("Trying to reconnect...")
                }

                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    println("Incoming message from $topic")
                }

                /* Called when an ack is received*/
                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    println("On delivery complete" + token.message.payload.toString())
                }
            })
            options.isAutomaticReconnect = true
            options.maxReconnectDelay = 500
            client.connect(options)
        } catch (e: MqttException) {
            println(e)
        }

        client.subscribe(topic, 1)
    }

    private var errorLog: LogRecord? = null
    private var errorCount = 0


    fun publish(logQueue: MutableList<LogRecord>) {
        val message = MqttMessage()
        var serializedList = mutableListOf<ByteArray>()

        println("***** There are ${logQueue.count()} elements in the queue ******")
        //GlobalScope.launch {
            //while (isActive) {
                try {
                    logQueue.forEach{el -> serializedList.add(serializer.toByteArray(el))}
                    addErrorLog(serializedList)

                    message.payload = serializedList. // come faccio a serializzare???
                    //println("Publishing...")
                    client.publish(topic, message.payload, 1, true, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            errorCount = 0
                            //logQueue.remove()
                            println("Success")
                            println("***** ${logQueue.count()} log records have been sent ******")
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            println("Error, failure")
                            errorCount += logQueue.count()
                            errorLog = LogRecord(3,"$errorCount has been lost due to connection error", mapOf("type" to "logs removed"), LocalDateTime.now())
                        }
                    })
                } catch (e: MqttException) {
                    println("Error Publishing to $topic: " + e.message)
                }
                //delay(4000L)
            //}
        //}

    }

    private fun addErrorLog(serializedList: MutableList<ByteArray>) {
        if (errorLog != null)  {
            serializedList.add(serializer.toByteArray(errorLog!!))
        }
        errorLog = null
    }

    fun disconnect() {
        client.disconnect()
    }
}