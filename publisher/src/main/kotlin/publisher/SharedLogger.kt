package publisher

interface SharedLogger {
    fun put(logRecord: LogRecord): Unit
}