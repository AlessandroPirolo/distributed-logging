package pirale.sharedlogger.publisher

interface LogRecordSerializer {
    fun toByteArray(logRecord: LogRecord): ByteArray
    fun parseFrom(arr: ByteArray): LogRecord

}