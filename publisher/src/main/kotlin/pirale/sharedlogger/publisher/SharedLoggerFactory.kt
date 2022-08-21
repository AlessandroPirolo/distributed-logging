package pirale.sharedlogger.publisher

import kotlinx.coroutines.channels.Channel
import org.eclipse.paho.client.mqttv3.*
import pirale.sharedlogger.publisher.impl.DummySharedLogger
import pirale.sharedlogger.publisher.impl.QueuedSharedLogger
import pirale.sharedlogger.publisher.impl.SimpleSharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import pirale.sharedlogger.publisher.serialization.LogRecordPBSerializer

class SharedLoggerFactory {
    fun create(): SharedLogger {

        val client = MqttAsyncClient(System.getProperties()["client"].toString(), MqttClient.generateClientId())
        val logRecordListMqttClient = LogRecordListMqttClient(client, System.getProperties()["topic"].toString(), MqttConnectOptions(), LogRecordPBSerializer())

        return when(System.getProperties()["type"]) {
            "dummy" -> DummySharedLogger()
            "simple" -> {
                SimpleSharedLogger(logRecordListMqttClient)
            }
            "queued" -> {
                val sender = SendChannelFactory(logRecordListMqttClient, Channel<LogRecord>(10)).create()
                QueuedSharedLogger(sender)
            }
            else -> {
                SimpleSharedLogger(logRecordListMqttClient)
            }
        }
    }
}