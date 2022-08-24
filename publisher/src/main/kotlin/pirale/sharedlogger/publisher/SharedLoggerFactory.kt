package pirale.sharedlogger.publisher

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import pirale.sharedlogger.publisher.impl.DummySharedLogger
import pirale.sharedlogger.publisher.impl.QueuedSharedLogger
import pirale.sharedlogger.publisher.impl.SimpleSharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import pirale.sharedlogger.publisher.serialization.LogRecordPBSerializer

class SharedLoggerFactory {
    fun create(): SharedLogger {
        val properties = System.getProperties()

        val connOption = MqttConnectOptions()
        connOption.isAutomaticReconnect = properties["autoReconnection"].toString().toBoolean()
        connOption.maxReconnectDelay = properties["reconnectionDelay"].toString().toInt()

        val client = MqttClient(properties["client"].toString(), MqttClient.generateClientId(), MqttDefaultFilePersistence("/tmp"))
        val logRecordListMqttClient = LogRecordListMqttClient(client, properties["topic"].toString(), connOption, LogRecordPBSerializer())

        return when(properties["type"]) {
            "dummy" -> DummySharedLogger()
            "simple" -> {
                SimpleSharedLogger(logRecordListMqttClient)
            }
            "queued" -> {
                QueuedSharedLogger(
                    logRecordListMqttClient,
                    Integer.parseInt(properties["channelSize"].toString()),
                    properties["delayMillis"].toString().toLong()
                )
            }
            else -> {
                SimpleSharedLogger(logRecordListMqttClient)
            }
        }
    }
}