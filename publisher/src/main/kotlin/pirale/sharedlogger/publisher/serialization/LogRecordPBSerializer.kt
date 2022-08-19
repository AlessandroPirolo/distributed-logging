package pirale.sharedlogger.publisher.serialization

import com.google.protobuf.Timestamp
import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.LogRecordSerializer
import pirale.sharedlogger.publisher.protobuf.LogOuterClass.Log
import pirale.sharedlogger.publisher.protobuf.LogOuterClass.Log.LogLevel
import pirale.sharedlogger.publisher.protobuf.LogOuterClass.Log.Tag
import pirale.sharedlogger.publisher.serialization.protobuf.LogKt
import pirale.sharedlogger.publisher.serialization.protobuf.log
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LogRecordPBSerializer : LogRecordSerializer {

    override fun toByteArray(logRecord: LogRecord): ByteArray {
        val log = getLog(logRecord)
        return log.toByteArray()
    }

    override fun parseFrom(arr: ByteArray): LogRecord {
        val log = Log.parseFrom(arr)
        return LogRecord(log.logLevel.number, log.msg, log.tagList.associate { it.key to it.value }, getDate(log.timeStamp))
    }


    private fun getTag(tags: Map<String, String>): List<Tag> {
        return tags.map {
            LogKt.tag {
                this.key = it.key
                this.value = it.value
            }
        }
    }

    private fun getLogLevel(logLevel: Int): LogLevel {
        return when(logLevel) {
            0 -> Log.LogLevel.TRACE
            1 -> Log.LogLevel.DEBUG
            2 -> Log.LogLevel.INFORMATION
            3 -> Log.LogLevel.WARNING
            4 -> Log.LogLevel.ERROR
            5 -> Log.LogLevel.CRITICAL
            6 -> Log.LogLevel.NONE
            else -> Log.LogLevel.INFORMATION
        }
    }

    private fun getLog(logRecord: LogRecord): Log {

        return log {
            this.tag += getTag(logRecord.tags)
            this.logLevel = getLogLevel(logRecord.logLevel)
            this.msg = logRecord.msg
            this.timeStamp = Timestamp.newBuilder().setSeconds(logRecord.time.second.toLong()).setNanos(logRecord.time.nano).build()
        }
    }

    private fun getDate(timestamp: Timestamp): LocalDateTime {
        return Instant
            .ofEpochSecond( timestamp.seconds, timestamp.nanos.toLong())
            .atZone( ZoneId.of( "Europe/Rome" ) )
            .toLocalDateTime()
    }


}