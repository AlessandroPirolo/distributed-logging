package publisher.impl

import org.eclipse.paho.client.mqttv3.*
import publisher.LogRecord
import publisher.LogRecordSerializer
import publisher.SharedLogger
import publisher.serialization.LogRecordPBSerializer

class SharedLoggerFactory(client: MqttClient, topic: String, serializer: LogRecordSerializer) : SharedLogger {

    val _client = client
    val _topic = topic
    val _serializer = serializer

    fun simpleMqttSharedLogger(client: MqttClient, topic: String, options: MqttConnectOptions, serializer: LogRecordSerializer): SharedLogger {
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
            println("e")
            e.printStackTrace()
        }

        client.subscribe(topic, 1)

        return SharedLoggerFactory(client, topic, serializer)
    }

    fun queuedMqttSharedLogger(client: MqttClient, topic: String, options: MqttConnectOptions, serializer: LogRecordSerializer): SharedLogger {

        return SharedLoggerFactory(client, topic, serializer)
    }

    override fun put(logRecord: LogRecord): Unit {
        try {
            println("Publishing...")
            val message = MqttMessage()
            message.payload = _serializer.toByteArray(logRecord)
            _client.publish(_topic, message.payload, 1, true)
            println("*** $logRecord published to $_topic ***")

        } catch (e: MqttException) {
            println("Error Publishing to $_topic: " + e.message)
            e.printStackTrace()
        }
    }
}