package pirale.sharedlogger.publisher.channel

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SendChannel
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient
import java.time.Instant

class SendLogChannel(private val logRecordListMqttClient: LogRecordListMqttClient, private val channel: Channel<LogRecord>) : SendChannel {
    override fun send() {
        GlobalScope.launch {
            val list = mutableListOf<LogRecord>()
            var lastSubmission = Instant.now()
            var last: LogRecord? = null
            channel.receiveAsFlow().combineTransform(ticker(50,0).receiveAsFlow().map { Instant.now() }) { s, t ->

                if (s !== last) {
                    list.add(s)
                    last = s
                }

                val now = Instant.now()
                val emitCondition = list.count() == 10 ||
                        (list.isNotEmpty() && lastSubmission.isBefore(t.minusMillis(50)))

                if (emitCondition) {
                    emit(list)
                    //println(list)
                    lastSubmission = Instant.now()
                    list.clear()
                }
            }.onEach { logRecordListMqttClient.publish(it) }.collect()
        }

    }
}