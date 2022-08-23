package pirale.sharedlogger.publisher.impl

import pirale.sharedlogger.publisher.LogRecord
import pirale.sharedlogger.publisher.SharedLogger

class DummySharedLogger : SharedLogger {

    override suspend fun put(logRecord: LogRecord) {
        println(logRecord)
    }
}