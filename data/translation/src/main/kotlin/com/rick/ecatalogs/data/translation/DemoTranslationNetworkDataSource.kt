package com.rick.ecatalogs.data.translation

import JvmUnitTestTranslationDemoAssetManager
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.InputStream
import javax.inject.Inject

fun interface DemoTranslationAssetManager {
    fun open(filename: String): InputStream
}

class DemoTranslationNetworkDataSource @Inject constructor(
    private val networkJson: Json,
    private val assets: DemoTranslationAssetManager = JvmUnitTestTranslationDemoAssetManager,
) : TranslationDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getTranslation(body: TranslationBody): TranslationResponse {
        val sample: TranslationResponse = assets.open("translation.json").use(networkJson::decodeFromStream)
        // Like the real API, one translation per text sent (the sample's, repeated as needed)
        return sample.copy(
            translations = List(body.texts.size) { sample.translations[it % sample.translations.size] },
        )
    }

}