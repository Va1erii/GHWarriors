package jp.vpopov.ghwarriors.core.logging

object Logging {
    private lateinit var logger: Logger

    fun d(
        throwable: Throwable? = null,
        message: () -> String
    ) = logger.d(throwable, message)

    fun i(
        throwable: Throwable? = null,
        message: () -> String
    ) = logger.i(throwable, message)

    fun w(
        throwable: Throwable? = null,
        message: () -> String
    ) = logger.w(throwable, message)

    fun e(
        throwable: Throwable? = null,
        message: () -> String
    ) = logger.e(throwable, message)

    fun v(
        throwable: Throwable? = null,
        message: () -> String
    ) = logger.v(throwable, message)

    fun wtf(
        throwable: Throwable? = null,
        message: () -> String
    ) = logger.wtf(throwable, message)

    fun setup(newLogger: Logger) {
        logger = newLogger
    }
}