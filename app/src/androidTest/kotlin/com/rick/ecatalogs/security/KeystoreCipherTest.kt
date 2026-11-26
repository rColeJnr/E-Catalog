package com.rick.ecatalogs.security

import android.util.Base64
import com.rick.ecatalogs.data.common.security.KeystoreCipher
import org.junit.After
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

/** [KeystoreCipher] against the device's real Android Keystore (it doesn't exist on the JVM). */
class KeystoreCipherTest {

    private val cipher = KeystoreCipher("ecs_test_keystore_cipher")

    @After
    fun deleteTestKey() = cipher.deleteKey()

    @Test
    fun whenEncryptedThenDecrypted_thenOriginalIsReturned() {
        val secret = "twitch-app-access-token-é✓"

        assertEquals(secret, cipher.decrypt(cipher.encrypt(secret)))
    }

    @Test
    fun whenEncrypted_thenStoredValueDoesNotContainThePlaintext() {
        val secret = "plain-token-value"

        val stored = cipher.encrypt(secret)

        assertFalse(secret in stored)
        assertFalse(secret in String(Base64.decode(stored, Base64.NO_WRAP), Charsets.ISO_8859_1))
    }

    @Test
    fun whenSameValueEncryptedTwice_thenCiphertextsDiffer() {
        // A fresh IV each time: equal tokens can't be spotted on disk
        assertNotEquals(cipher.encrypt("same"), cipher.encrypt("same"))
    }

    @Test
    fun tamperedValue_whenDecrypted_thenNullIsReturned() {
        val bytes = Base64.decode(cipher.encrypt("token"), Base64.NO_WRAP)
        bytes[bytes.lastIndex] = (bytes[bytes.lastIndex].toInt() xor 1).toByte()

        assertNull(cipher.decrypt(Base64.encodeToString(bytes, Base64.NO_WRAP)))
    }

    @Test
    fun keyDeleted_whenDecrypted_thenNullIsReturned() {
        // Like app data restored on another device: the ciphertext is there, the key isn't
        val stored = cipher.encrypt("token")
        cipher.deleteKey()

        assertNull(cipher.decrypt(stored))
    }

    @Test
    fun garbage_whenDecrypted_thenNullIsReturned() {
        assertNull(cipher.decrypt("not base64 at all!"))
        assertNull(cipher.decrypt(Base64.encodeToString(ByteArray(5), Base64.NO_WRAP)))
    }
}
