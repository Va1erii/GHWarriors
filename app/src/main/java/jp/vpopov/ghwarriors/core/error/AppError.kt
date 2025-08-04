package jp.vpopov.ghwarriors.core.error

/**
 * Sealed interface representing application-level errors.
 * All errors in the application should implement this interface for consistent error handling.
 */
sealed interface AppError {
    /** The underlying throwable that caused this error, if any */
    val cause: Throwable?
}

/**
 * Represents an unexpected error that doesn't fall into any specific category.
 * This is a fallback for errors that cannot be classified into more specific types.
 */
class UnknownError(override val cause: Throwable?) : AppError

/**
 * Sealed class representing network-related errors.
 * Contains specific error types for different network failure scenarios.
 */
sealed class NetworkError : AppError {
    /**
     * Indicates that the device has no internet connectivity.
     */
    class NoInternetConnection(override val cause: Throwable? = null) : NetworkError()

    /**
     * Indicates that a network request timed out.
     */
    class Timeout(override val cause: Throwable? = null) : NetworkError()

    /**
     * Indicates a server-side error with an HTTP status code.
     *
     * @param code The HTTP status code returned by the server
     */
    class ServerError(val code: Int, override val cause: Throwable? = null) : NetworkError()
}