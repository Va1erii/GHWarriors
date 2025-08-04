package jp.vpopov.ghwarriors.core.logging.timber

import android.util.Log
import jp.vpopov.ghwarriors.BuildConfig
import jp.vpopov.ghwarriors.core.logging.Logger
import jp.vpopov.ghwarriors.core.logging.LoggingManager
import timber.log.Timber
import javax.inject.Inject

class TimberLoggingManager @Inject constructor() : LoggingManager {
    override fun setup() {
        Timber.plant(
            if (BuildConfig.DEBUG) {
                Timber.DebugTree()
            } else {
                ReleaseTree()
            }
        )
    }

    override fun withTag(tag: String): Logger {
        return createLogger(tag)
    }

    private fun createLogger(tag: String): Logger {
        return object : Logger {
            override fun log(priority: Int, throwable: Throwable?, message: () -> String) {
                Timber.tag(tag)
                Timber.log(priority = priority, message = message(), t = throwable)
            }
        }
    }

    class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            if (Log.isLoggable(tag, priority)) {
                val stackTrace = Log.getStackTraceString(t)
                val fullMessage = if (stackTrace.isNotBlank()) {
                    "$message\n$stackTrace"
                } else {
                    message
                }
                Log.println(priority, tag, fullMessage)
            }
        }
    }
}