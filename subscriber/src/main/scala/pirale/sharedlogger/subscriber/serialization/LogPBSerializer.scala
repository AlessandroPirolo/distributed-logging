package pirale.sharedlogger.subscriber.serialization

import com.google.protobuf.timestamp.Timestamp
import pirale.sharedlogger.subscriber.{LogRecord, LogSerializer}
import protobuf.log.Log
import protobuf.log.Log.{LogLevel, Tag}

import java.time.{Instant, LocalDateTime, ZoneId}

class LogPBSerializer extends LogSerializer {

  override def toByte(logRecord: LogRecord): Array[Byte] = {
    val log = getLog(logRecord)
    log.toByteArray
  }

  override def parseFrom(arr: Array[Byte]): LogRecord = {
    val tmpLog = Log.parseFrom(arr)
    val date = Instant
      .ofEpochSecond(tmpLog.timeStamp.get.seconds, tmpLog.timeStamp.get.nanos)
      .atZone(ZoneId.of("Europe/Rome"))
      .toLocalDateTime

    LogRecord(tmpLog.logLevel.value, tmpLog.msg, tmpLog.tag.map(tag => tag.key -> tag.value).toMap, date)
  }

  private def getLogLevel(logLevel: Int): Log.LogLevel =
    logLevel match {
      case 0 => LogLevel.TRACE
      case 1 => LogLevel.DEBUG
      case 2 => LogLevel.INFORMATION
      case 3 => LogLevel.WARNING
      case 4 => LogLevel.ERROR
      case 5 => LogLevel.CRITICAL
      case 6 => LogLevel.NONE
      case _ => LogLevel.INFORMATION
    }

  private def getTags(tags: Map[String, String]): Seq[Log.Tag] =
    tags.map(el => Tag(el._1, el._2)).toList

  private def getTimestamp(timestamp: LocalDateTime): Option[Timestamp] =
    Some(
      Timestamp()
        .withNanos(timestamp.getNano)
        .withSeconds(timestamp.getSecond)
    )

  private def getLog(logRecord: LogRecord): Log =
    Log(
      getLogLevel(logRecord.logLevel),
      logRecord.msg,
      getTags(logRecord.tags),
      getTimestamp(logRecord.timestamp)
    )
}
