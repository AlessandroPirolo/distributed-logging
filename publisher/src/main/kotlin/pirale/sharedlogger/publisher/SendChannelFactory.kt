package pirale.sharedlogger.publisher

import kotlinx.coroutines.channels.Channel
import pirale.sharedlogger.publisher.channel.SendLogChannel
import pirale.sharedlogger.publisher.mqtt.LogRecordListMqttClient

class SendChannelFactory(private val logRecordListMqttClient: LogRecordListMqttClient, private val channel: Channel<LogRecord>) {
    fun create(): SendChannel {
        return SendLogChannel(logRecordListMqttClient, channel)
    }
}