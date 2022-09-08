package pirale.sharedlogger.subscriber

abstract class LogSerializer {

  def toByte(logRecord: LogRecord): Array[Byte]
  def parseFrom(arr: Array[Byte]): LogRecord

}
