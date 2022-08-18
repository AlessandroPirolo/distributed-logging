package publisher

import java.time.LocalDateTime

data class LogRecord(val logLevel: Int, val msg: String, val tags: Map<String, String>, val time: LocalDateTime)