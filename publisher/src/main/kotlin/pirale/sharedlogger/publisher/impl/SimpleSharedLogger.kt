package pirale.sharedlogger.publisher.impl

import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import java.util.concurrent.LinkedBlockingQueue

class SimpleSharedLogger(private val logRecordListMqttClient: LogRecordListMqttClient) : SharedLogger {

    override suspend fun put(logRecord: LogRecord) {
        /*var queue = LinkedBlockingQueue<List<LogRecord>>(1)
        queue.add(listOf(logRecord))*/
        logRecordListMqttClient.publish(mutableListOf(logRecord) )
    }
}