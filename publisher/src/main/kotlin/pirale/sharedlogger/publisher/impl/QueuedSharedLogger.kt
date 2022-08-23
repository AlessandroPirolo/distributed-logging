package pirale.sharedlogger.publisher.impl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttException
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import java.time.Instant

class QueuedSharedLogger(private val mqttClient: LogRecordListMqttClient, size: Int, delayMillis: Long) : SharedLogger {
    private val channel: Channel<LogRecord>
    private val flow: Flow<List<LogRecord>>
    private lateinit var errorLog : LogRecord
    init {
        this.channel = Channel<LogRecord>(size)
        val list = mutableListOf<LogRecord>()
        var lastSubmission = Instant.now()
        var last: LogRecord? = null
        val ticker = ticker(delayMillis,0).receiveAsFlow().map { Instant.now() }
        this.flow = channel.receiveAsFlow().combineTransform(ticker) { lr, t ->
            if (lr !== last) {
                list.add(lr)
                last = lr
            }
            val emitCondition = list.count() == 10 ||
                    (list.isNotEmpty() && lastSubmission.isBefore(t.minusMillis(500)))
            if (emitCondition) {
                emit(list)
                lastSubmission = Instant.now()
                list.clear()
            }
        }.onEach { lrs ->
            try {
                mqttClient.publish(lrs)
            } catch (e: MqttException) {
                println("Failed to publish the message")
            }
        }
        GlobalScope.launch { flow.collect() }

    }

    override suspend fun put(logRecord: LogRecord) {
        channel.send(logRecord)
    }
}