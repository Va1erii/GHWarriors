package jp.vpopov.ghwarriors.core.logging

import android.util.Log

interface Logger {
    fun log(priority: Int, throwable: Throwable? = null, message: () -> String)
}

fun Logger.d(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.DEBUG, throwable, message)

fun Logger.i(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.INFO, throwable, message)

fun Logger.w(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.WARN, throwable, message)

fun Logger.e(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.ERROR, throwable, message)


fun Logger.v(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.VERBOSE, throwable, message)

fun Logger.wtf(
    throwable: Throwable? = null,
    message: () -> String
) = log(Log.ASSERT, throwable, message)