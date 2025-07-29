package jp.vpopov.ghwarriors.core.error

import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

object ErrorMapper {
    fun convert(exception: Exception): AppError {
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