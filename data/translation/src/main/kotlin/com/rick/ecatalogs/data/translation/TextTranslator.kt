package com.rick.ecatalogs.data.translation

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Translates catalog text (overviews, synopses, biographies…) into the phone's language.
 *
 * Every API the app reads is in English, so there's nothing to translate when the phone is
 * in English: check [isAvailable] before offering a translation. Results are kept for the
 * session, so toggling back and forth or scrolling past a card again costs no request.
 */
@Singleton
class TextTranslator @Inject constructor(
    private val repository: TranslationRepository,
) {

    /** ISO 639-1 code of the language text is translated into, e.g. "pt". */
    val targetLanguage: String get() = Locale.getDefault().language

    val isAvailable: Boolean
        get() = targetLanguage.isNotBlank() && !targetLanguage.equals(SOURCE_LANGUAGE, ignoreCase = true)

    // Least recently used first; plain JVM so it works in unit tests
    private val cache = object : LinkedHashMap<String, String>(CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>) = size > CACHE_SIZE
    }

    /** [text] in [targetLanguage], or null when it can't be translated (offline, service error). */
    suspend fun translate(text: String): String? = translate(listOf(text))?.single()

    /**
     * [texts] in [targetLanguage], in the same order, translated in a single request;
     * or null when any of them can't be. Blank texts come back as they are.
     */
    suspend fun translate(texts: List<String>): List<String>? {
        if (!isAvailable) return null
        val language = targetLanguage
        val missing = synchronized(cache) {
            texts.filter { it.isNotBlank() && cache[key(language, it)] == null }.distinct()
        }

        if (missing.isNotEmpty()) {
            val translations = try {
                repository.getTranslation(
                    TranslationBody(folderId = FOLDER_ID, targetLanguageCode = language, texts = missing),
                ).firstOrNull()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null // offline, or a malformed response
            }
            if (translations == null || translations.size != missing.size) return null
            synchronized(cache) {
                missing.zip(translations).forEach { (original, translation) ->
                    cache[key(language, original)] = translation.text
                }
            }
        }
        return synchronized(cache) {
            texts.map { if (it.isBlank()) it else cache[key(language, it)] ?: return null }
        }
    }

    private fun key(language: String, text: String) = "$language\u0000$text"
}

private const val SOURCE_LANGUAGE = "en"
private const val CACHE_SIZE = 200
private const val FOLDER_ID = "b1g3qbtiprdplbrpod5i"
