package com.rick.ecatalogs.movie.data_movie.network.retrofit

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Appends an API key query parameter to every request, so callers never handle the key.
 * Keys come from local.properties via BuildConfig (see this module's build.gradle.kts).
 */
internal class ApiKeyInterceptor(
    private val parameterName: String,
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.newBuilder()
            .addQueryParameter(parameterName, apiKey)
            .build()
        return chain.proceed(request.newBuilder().url(url).build())
    }
}
