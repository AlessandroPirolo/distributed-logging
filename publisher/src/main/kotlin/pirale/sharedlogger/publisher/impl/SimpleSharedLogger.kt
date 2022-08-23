package pirale.sharedlogger.publisher.impl

import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import java.util.concurrent.LinkedBlockingQueue

class SimpleSharedLogger(private val logRecordListMqttClient: LogRecordListMqttClient) : SharedLogger {

    override suspend fun put(logRecord: LogRecord) {
        logRecordListMqttClient.publish(mutableListOf(logRecord) )
    }
}