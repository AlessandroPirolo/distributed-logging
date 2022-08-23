package pirale.sharedlogger.publisher

interface SharedLogger {
    suspend fun put(logRecord: LogRecord)
}