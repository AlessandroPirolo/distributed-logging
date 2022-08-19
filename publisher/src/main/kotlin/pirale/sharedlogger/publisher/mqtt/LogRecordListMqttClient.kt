package pirale.sharedlogger.publisher.mqtt

import org.eclipse.paho.client.mqttv3.*
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.LogRecordSerializer
import java.util.*

class LogRecordListMqttClient(private val client: MqttClient, private val topic: String, private val options: MqttConnectOptions, private val serializer: LogRecordSerializer) {

    private fun connect() {
        try {
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    println("conn Completed")
                    println("Connected to: $serverURI")
                }

                override fun connectionLost(cause: Throwable) {
                    println("The Connection was lost.")
                }

                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    println("Incoming message from $topic")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {

                }
            })
            client.connect(options)
        } catch (e: MqttException) {
            println(e)
            e.printStackTrace()
        }
        
        client.subscribe(topic, 1)

    }

    fun send(logQueue: Queue<LogRecord>) {
        if (!client.isConnected)    {
            connect()
        }

        try {
            println("Publishing...")
            while(logQueue.isNotEmpty())    {
                val logRecord = logQueue.poll()
                val message = MqttMessage()
                message.payload = serializer.toByteArray(logRecord)
                client.publish(topic, message.payload, 1, true)
                println("*** $logRecord published to $topic ***")
            }


        } catch (e: MqttException) {
            println("Error Publishing to $topic: " + e.message)
            e.printStackTrace()
        }
    }
}