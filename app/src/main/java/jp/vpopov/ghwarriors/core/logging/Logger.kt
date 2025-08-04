package jp.vpopov.ghwarriors.core.logging

import android.util.Log

/**
 * A logging interface that provides methods for logging messages with different priority levels.
 * All logging operations use lazy message evaluation to avoid string creation when logging is disabled.
 */
interface Logger {
    /**
     * Logs a message with the specified priority level.
     *
     * @param priority The log priority level (use Android Log constants like Log.DEBUG, Log.INFO, etc.)
     * @param throwable Optional throwable to include in the log entry
     * @param message Lazy-evaluated message supplier to avoid string creation when logging is disabled
     */
    fun log(priority: Int, throwable: Throwable? = null, message: () -> String)
}

/**
 * Logs a debug message.
 *
 * @param throwable Optional throwable to include in the log entry
 * @param message Lazy-evaluated debug message
 */
fun Logger.d(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.DEBUG, throwable, message)

/**
 * Logs an info message.
 *
 * @param throwable Optional throwable to include in the log entry
 * @param message Lazy-evaluated info message
 */
fun Logger.i(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.INFO, throwable, message)

/**
 * Logs a warning message.
 *
 * @param throwable Optional throwable to include in the log entry
 * @param message Lazy-evaluated warning message
 */
fun Logger.w(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.WARN, throwable, message)

/**
 * Logs an error message.
 *
 * @param throwable Optional throwable to include in the log entry
 * @param message Lazy-evaluated error message
 */
fun Logger.e(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.ERROR, throwable, message)


/**
 * Logs a verbose message.
 *
 * @param throwable Optional throwable to include in the log entry
 * @param message Lazy-evaluated verbose message
 */
fun Logger.v(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.VERBOSE, throwable, message)

/**
 * Logs a "What a Terrible Failure" message (assert level).
 * This should be used for conditions that should never happen.
 *
 * @param throwable Optional throwable to include in the log entry
 * @param message Lazy-evaluated assert message
 */
fun Logger.wtf(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.ASSERT, throwable, message)