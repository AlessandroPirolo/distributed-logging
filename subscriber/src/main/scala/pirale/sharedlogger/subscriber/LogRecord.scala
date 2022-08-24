package pirale.sharedlogger.subscriber

import java.time.LocalDateTime

final case class LogRecord(logLevel: Int, msg: String, tags: Map[String, String], timestamp: LocalDateTime)
