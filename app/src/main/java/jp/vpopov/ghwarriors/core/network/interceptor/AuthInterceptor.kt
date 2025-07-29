package jp.vpopov.ghwarriors.core.network.interceptor

import jp.vpopov.ghwarriors.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = BuildConfig.ACCESS_TOKEN
        val modified = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("X-GitHub-Api-Version", "2022-11-28")
            .build()
        return chain.proceed(modified)
    }
}