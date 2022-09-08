package pirale.sharedlogger.publisher.impl

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.MqttException
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SharedLogger
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import java.time.Instant
import java.time.LocalDateTime

class QueuedSharedLogger(private val mqttClient: LogRecordListMqttClient, size: Int, delayMillis: Long) : SharedLogger {
    private val channel: Channel<LogRecord>
    private val flow: Flow<List<LogRecord>>
    private var errorLog : MutableList<LogRecord> = mutableListOf()
    private var errorCount = 0

    init {
        this.channel = Channel(size)
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
                    (list.isNotEmpty() && lastSubmission.isBefore(t.minusMillis(450)))
            if (emitCondition) {
                emit(list)
                lastSubmission = Instant.now()
                list.clear()
            }
        }.onEach { lrs ->
            if(errorLog.isNotEmpty())    {
                if(publish(errorLog)) {
                    errorLog = mutableListOf()
                    errorCount = 0
                    publish(lrs)
                }
                else {
                    errorCount += lrs.count()
                }
            }
            else    {
                publish(lrs)
            }

        }
        GlobalScope.launch { flow.collect() }

    }

    private fun setErrorLog() {
        errorLog = mutableListOf()
        errorLog.add(
            LogRecord(
                4,
                "$errorCount logs have been lost",
                mapOf<String, String>("type" to "logs deleted"),
                LocalDateTime.now()
            )
        )
    }

    private fun publish(lrs: MutableList<LogRecord>): Boolean {
        var isSuccess: Boolean
        try {
            mqttClient.publish(lrs)
            isSuccess = true
        } catch (e: MqttException) {
            isSuccess = false
            if(lrs[0].logLevel != 4) { errorCount += lrs.count() }
            setErrorLog()
        }
        return isSuccess
    }

    override suspend fun put(logRecord: LogRecord) {
        channel.send(logRecord)
    }
}