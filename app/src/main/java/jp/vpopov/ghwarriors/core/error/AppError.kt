package jp.vpopov.ghwarriors.core.error

sealed interface AppError {
    val cause: Throwable?
}

class UnknownError(override val cause: Throwable?) : AppError

sealed class NetworkError : AppError {
    class NoInternetConnection(override val cause: Throwable? = null) : NetworkError()
    class Timeout(override val cause: Throwable? = null) : NetworkError()
    class ServerError(val code: Int, override val cause: Throwable? = null) : NetworkError()
}