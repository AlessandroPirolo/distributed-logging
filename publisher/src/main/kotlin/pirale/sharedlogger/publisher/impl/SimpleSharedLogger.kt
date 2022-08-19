package pirale.sharedlogger.publisher.impl

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.LogRecordSerializer
import pirale.sharedlogger.publisher.SharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import java.util.LinkedList

class SimpleSharedLogger(private val client: MqttClient, private val topic: String, private val options: MqttConnectOptions, private val serializer: LogRecordSerializer) : SharedLogger {

    override fun put(logRecord: LogRecord) {
        val mqttClient = LogRecordListMqttClient(client, topic, options, serializer)
        val logQueue = LinkedList<LogRecord>()
        logQueue.add(logRecord)
        mqttClient.send(logQueue)
    }
}