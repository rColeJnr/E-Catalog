package com.rick.ecatalogs.game.data_game.network.retrofit

import com.rick.ecatalogs.game.data_game.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/** Adds IGDB's `Client-ID` and `Authorization: Bearer` headers; on a 401 gets a new token and retries once. */
internal class IgdbAuthInterceptor(
    private val tokens: TwitchTokenProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokens.token()
        val response = chain.proceed(chain.withToken(token))
        if (response.code != 401) return response
        response.close()
        tokens.invalidate(token)
        return chain.proceed(chain.withToken(tokens.token()))
    }

    private fun Interceptor.Chain.withToken(token: String) = request().newBuilder()
        .header("Client-ID", BuildConfig.IGDB_CLIENT_ID)
        .header("Authorization", "Bearer $token")
        .build()
}
