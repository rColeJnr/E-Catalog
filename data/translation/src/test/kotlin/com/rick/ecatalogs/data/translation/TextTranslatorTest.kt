package com.rick.ecatalogs.data.translation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TextTranslatorTest {

    private val defaultLocale = Locale.getDefault()
    private val repository = FakeTranslationRepository()
    private val subject = TextTranslator(repository)

    @Before
    fun setUp() = Locale.setDefault(Locale.forLanguageTag("pt-BR"))

    @After
    fun tearDown() = Locale.setDefault(defaultLocale)

    @Test
    fun translate_sendsTheTextsInOneRequest_toThePhonesLanguage() = runTest {
        assertEquals(listOf("pt:Dune", "pt:Arrival"), subject.translate(listOf("Dune", "Arrival")))
        assertEquals(listOf(listOf("Dune", "Arrival")), repository.requests.map { it.texts })
        assertEquals("pt", repository.requests.single().targetLanguageCode)
    }

    @Test
    fun translate_remembersTranslations() = runTest {
        subject.translate("Dune")
        assertEquals(listOf("pt:Dune", "pt:Arrival"), subject.translate(listOf("Dune", "Arrival")))

        // Only the text it hadn't seen is sent
        assertEquals(listOf(listOf("Dune"), listOf("Arrival")), repository.requests.map { it.texts })
    }

    @Test
    fun translate_keepsBlankTexts_withoutSendingThem() = runTest {
        assertEquals(listOf("pt:Dune", ""), subject.translate(listOf("Dune", "")))
        assertEquals(listOf(listOf("Dune")), repository.requests.map { it.texts })
    }

    @Test
    fun translate_inEnglish_isNotAvailable() = runTest {
        Locale.setDefault(Locale.US)

        assertFalse(subject.isAvailable)
        assertNull(subject.translate("Dune"))
        assertTrue(repository.requests.isEmpty())
    }

    @Test
    fun translate_whenOffline_isNull() = runTest {
        repository.failure = IOException("offline")

        assertNull(subject.translate("Dune"))
    }

    @Test
    fun translate_whenTheServiceAnswersNothing_isNull() = runTest {
        repository.answersNothing = true // what TranslationRepositoryImpl does on an HTTP error

        assertNull(subject.translate("Dune"))
    }

    @Test
    fun translate_afterAFailure_triesAgain() = runTest {
        repository.failure = IOException("offline")
        subject.translate("Dune")
        repository.failure = null

        assertEquals("pt:Dune", subject.translate("Dune"))
    }

    @Test
    fun translate_isPerLanguage() = runTest {
        subject.translate("Dune")
        Locale.setDefault(Locale.forLanguageTag("ru"))

        assertEquals("ru:Dune", subject.translate("Dune"))
    }

    private class FakeTranslationRepository : TranslationRepository {
        val requests = mutableListOf<TranslationBody>()
        var failure: Exception? = null
        var answersNothing = false

        override fun getTranslation(body: TranslationBody): Flow<List<Translation>> {
            requests += body
            if (answersNothing) return emptyFlow()
            return flow {
                failure?.let { throw it }
                emit(body.texts.map { Translation("${body.targetLanguageCode}:$it", "en") })
            }
        }
    }
}
