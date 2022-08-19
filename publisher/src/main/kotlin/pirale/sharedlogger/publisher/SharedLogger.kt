package pirale.sharedlogger.publisher

interface SharedLogger {
    fun put(logRecord: LogRecord): Unit
}