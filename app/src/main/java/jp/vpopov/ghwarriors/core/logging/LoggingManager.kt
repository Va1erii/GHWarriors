package jp.vpopov.ghwarriors.core.logging

import android.util.Log
import jp.vpopov.ghwarriors.BuildConfig
import timber.log.Timber

interface LoggingManager {
    fun setup()
}

class TimberLoggingManager : LoggingManager {
    val logger = object : Logger {
        override fun log(
            priority: Int,
            throwable: Throwable?,
            message: () -> String
        ) {
            Timber.log(priority, message(), throwable)
        }
    }

    override fun setup() {
        Timber.plant(
            if (BuildConfig.DEBUG) {
                Timber.DebugTree()
            } else {
                ReleaseTree()
            }
        )
        Logging.setup(logger)
    }

    class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (Log.isLoggable(tag, priority)) {
                var stackTrace = Log.getStackTraceString(t)
                var message = if (stackTrace.isNotBlank()) {
                    "$message\n$stackTrace"
                } else {
                    message
                }
                Log.println(priority, tag, message)
            }
        }
    }
}