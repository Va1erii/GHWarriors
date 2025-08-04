package jp.vpopov.ghwarriors.core.error

import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

/**
 * Utility object for mapping platform exceptions to application-specific error types.
 * This centralizes error conversion logic and ensures consistent error handling across the app.
 */
object ErrorMapper {
    /**
     * Converts platform exceptions to application-specific error types.
     * 
     * This method maps common exceptions to their corresponding AppError types:
     * - SocketTimeoutException -> NetworkError.Timeout
     * - IOException -> NetworkError.NoInternetConnection  
     * - HttpException -> NetworkError.ServerError
     * - All other exceptions -> UnknownError
     * 
     * @param exception The platform exception to convert
     * @return The corresponding AppError instance
     */
    fun convert(exception: Throwable): AppError {
        return when (exception) {
            is SocketTimeoutException -> NetworkError.Timeout(exception)
            is IOException -> NetworkError.NoInternetConnection(exception)
            is HttpException -> {
                val code = exception.code()
                NetworkError.ServerError(code)
            }

            else -> UnknownError(exception)
        }
    }
}