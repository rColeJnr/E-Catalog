package com.rick.ecatalogs.data.common.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.GeneralSecurityException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Encrypts small secrets (tokens) before they are written to disk, with an AES-256-GCM key kept in
 * the Android Keystore. The key never leaves the Keystore (hardware-backed where the device has it),
 * so a copy of the app's files, a backup or a rooted read of SharedPreferences only shows ciphertext.
 *
 * Output is Base64 of `IV (12 bytes) + ciphertext + GCM tag`; GCM also detects tampering.
 *
 * [decrypt] returns null instead of throwing when the value can't be read: the key is gone (app data
 * restored on another device, Keystore reset, key alias deleted) or the value was altered. Callers
 * treat that as "nothing stored" and fetch the secret again.
 */
class KeystoreCipher(private val keyAlias: String) {

    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        // The Keystore picks a fresh random IV for every encryption
        cipher.init(Cipher.ENCRYPT_MODE, key())
        val iv = cipher.iv
        check(iv.size == IV_SIZE) { "Unexpected GCM IV size ${iv.size}" }
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + ciphertext, Base64.NO_WRAP)
    }

    fun decrypt(encoded: String): String? = try {
        val bytes = Base64.decode(encoded, Base64.NO_WRAP)
        if (bytes.size <= IV_SIZE) {
            null
        } else {
            val key = existingKey()
            if (key == null) {
                null
            } else {
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, bytes, 0, IV_SIZE))
                String(cipher.doFinal(bytes, IV_SIZE, bytes.size - IV_SIZE), Charsets.UTF_8)
            }
        }
    } catch (_: GeneralSecurityException) {
        null // altered, or encrypted with a key that has since been replaced
    } catch (_: IllegalArgumentException) {
        null // not Base64
    }

    /** Removes the key; anything encrypted with it can no longer be read. */
    fun deleteKey() {
        keyStore().deleteEntry(keyAlias)
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private fun existingKey(): SecretKey? = keyStore().getKey(keyAlias, null) as? SecretKey

    private fun key(): SecretKey = existingKey() ?: KeyGenerator
        .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
        .apply {
            init(
                KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_BITS)
                    .build()
            )
        }
        .generateKey()

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEY_BITS = 256
        const val IV_SIZE = 12
        const val TAG_BITS = 128
    }
}
