package pirale.sharedlogger.publisher.impl

import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import java.util.LinkedList

class SimpleSharedLogger(private val logRecordListMqttClient: LogRecordListMqttClient) : SharedLogger {

    override fun put(logRecord: LogRecord) {
        val logQueue = LinkedList<LogRecord>()
        logQueue.add(logRecord)
        logRecordListMqttClient.publish(logQueue)
    }
}