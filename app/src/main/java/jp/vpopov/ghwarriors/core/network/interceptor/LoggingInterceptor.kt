package jp.vpopov.ghwarriors.core.network.interceptor

import jp.vpopov.ghwarriors.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject

class LoggingInterceptor @Inject constructor() : Interceptor {
    private val delegate by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return delegate.intercept(chain)
    }
}