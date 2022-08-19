package pirale.sharedlogger.publisher

import org.eclipse.paho.client.mqttv3.*
import pirale.sharedlogger.publisher.impl.DummySharedLogger
import pirale.sharedlogger.publisher.impl.QueuedSharedLogger
import pirale.sharedlogger.publisher.impl.SimpleSharedLogger
import pirale.sharedlogger.publisher.serialization.LogRecordPBSerializer

class SharedLoggerFactory {
    fun create(): SharedLogger {
        return when(System.getProperties()["type"]) {
            "dummy" -> DummySharedLogger()
            "simple" -> {
                val client = MqttClient(System.getProperties()["client"].toString(), MqttClient.generateClientId())
                SimpleSharedLogger(client, System.getProperties()["topic"].toString(), MqttConnectOptions(), LogRecordPBSerializer())
            }
            "queued" -> QueuedSharedLogger()
            else -> {
                val client = MqttClient(System.getProperties()["client"].toString(), MqttClient.generateClientId())
                SimpleSharedLogger(client, System.getProperties()["topic"].toString(), MqttConnectOptions(), LogRecordPBSerializer())
            }
        }

    }

    /*fun simpleMqttSharedLogger(client: MqttClient, topic: String, options: MqttConnectOptions, serializer: LogRecordSerializer): SharedLogger {
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
    }*/
}