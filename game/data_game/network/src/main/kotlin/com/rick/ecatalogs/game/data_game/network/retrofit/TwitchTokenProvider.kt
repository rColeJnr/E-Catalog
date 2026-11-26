package com.rick.ecatalogs.game.data_game.network.retrofit

import android.content.Context
import com.google.gson.Gson
import com.rick.ecatalogs.data.common.security.KeystoreCipher
import com.rick.ecatalogs.game.data_game.network.BuildConfig
import com.rick.ecatalogs.game.data_game.network.model.TwitchTokenResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The app access token IGDB requires (Twitch client-credentials flow,
 * https://api-docs.igdb.com/#authentication). It lasts about 60 days, so it is kept in private
 * SharedPreferences and only fetched again when it is about to expire or IGDB rejects it.
 *
 * The token is stored encrypted with a Keystore key ([KeystoreCipher]); only its expiry time is
 * plain. If it can't be decrypted (app data restored on another device, Keystore reset) a new one
 * is fetched. A plaintext token saved by an earlier version is encrypted on first read.
 *
 * Called from an OkHttp interceptor, so it is synchronous.
 */
@Singleton
internal class TwitchTokenProvider @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val cipher = KeystoreCipher(KEY_ALIAS)
    private val client = OkHttpClient()
    private val gson = Gson()

    @Synchronized
    fun token(): String {
        val stored = storedToken()
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        if (stored != null && System.currentTimeMillis() < expiresAt - EXPIRY_MARGIN_MILLIS) return stored
        return fetch()
    }

    /** IGDB answered 401 with [rejected]: drop it, unless another request already replaced it. */
    @Synchronized
    fun invalidate(rejected: String) {
        if (storedToken() == rejected) clear()
    }

    private fun storedToken(): String? {
        prefs.getString(KEY_LEGACY_PLAIN_TOKEN, null)?.let { plain ->
            // Saved in the clear by an earlier version: encrypt it and drop the plain copy
            prefs.edit().putString(KEY_TOKEN, cipher.encrypt(plain)).remove(KEY_LEGACY_PLAIN_TOKEN).apply()
            return plain
        }
        val encrypted = prefs.getString(KEY_TOKEN, null) ?: return null
        return cipher.decrypt(encrypted) ?: run {
            clear() // unreadable here: fetch a new one
            null
        }
    }

    private fun clear() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_LEGACY_PLAIN_TOKEN).remove(KEY_EXPIRES_AT).apply()
    }

    private fun fetch(): String {
        if (BuildConfig.IGDB_CLIENT_ID.isBlank() || BuildConfig.IGDB_CLIENT_SECRET.isBlank()) {
            throw IOException("IGDB credentials missing: set igdbClientId and igdbClientSecret in local.properties")
        }
        val request = Request.Builder()
            .url(TOKEN_URL)
            .post(
                FormBody.Builder()
                    .add("client_id", BuildConfig.IGDB_CLIENT_ID)
                    .add("client_secret", BuildConfig.IGDB_CLIENT_SECRET)
                    .add("grant_type", "client_credentials")
                    .build()
            )
            .build()
        val body = client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Twitch token request failed: HTTP ${response.code}")
            response.body?.string() ?: throw IOException("Twitch token response is empty")
        }
        val token = gson.fromJson(body, TwitchTokenResponse::class.java)
        prefs.edit()
            .putString(KEY_TOKEN, cipher.encrypt(token.accessToken))
            .putLong(KEY_EXPIRES_AT, System.currentTimeMillis() + token.expiresIn * 1_000)
            .apply()
        return token.accessToken
    }
}

private const val TOKEN_URL = "https://id.twitch.tv/oauth2/token"
private const val PREFS = "igdb_auth"
private const val KEY_TOKEN = "access_token_encrypted"
/** Where versions before the encryption kept the token in plain text. */
private const val KEY_LEGACY_PLAIN_TOKEN = "access_token"
private const val KEY_ALIAS = "igdb_access_token_key"
private const val KEY_EXPIRES_AT = "expires_at"
/** Renew a day early, so a request never starts with a token that expires on the way. */
private const val EXPIRY_MARGIN_MILLIS = 24 * 60 * 60 * 1_000L
