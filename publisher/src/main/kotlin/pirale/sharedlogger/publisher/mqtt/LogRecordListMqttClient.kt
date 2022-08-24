package pirale.sharedlogger.publisher.mqtt

import org.eclipse.paho.client.mqttv3.*
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.LogRecordSerializer

class LogRecordListMqttClient(
    private val client: IMqttClient,
    private val topic: String,
    private val options: MqttConnectOptions,
    private val serializer: LogRecordSerializer) {

    init {
        connect()
    }

    private object callback : MqttCallbackExtended {
        override fun connectComplete(reconnect: Boolean, serverURI: String?) {}
        override fun connectionLost(cause: Throwable) {
            //println("Connection lost")
        }
        override fun messageArrived(topic: String, message: MqttMessage) {}
        override fun deliveryComplete(token: IMqttDeliveryToken) {}
    }

    private fun connect() {
        client.setCallback(callback)
        client.connect(options)
        client.subscribe(topic, 1)
    }

    fun publish(logs: MutableList<LogRecord>) {
        MqttMessage().apply {
            this.payload = logs.map { log -> serializer.toByteArray(log) }.reduce { ba1, ba2 -> ba1 + ba2 }
        }.run {
            client.publish(topic, payload, 1, false)
        }
    }

    fun disconnect() {
        client.disconnect()
    }
}