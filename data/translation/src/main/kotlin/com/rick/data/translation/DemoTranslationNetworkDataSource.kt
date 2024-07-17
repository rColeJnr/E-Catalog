package com.rick.data.translation

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
    override suspend fun getTranslation(body: TranslationBody): TranslationResponse =
        assets.open("translation.json").use(networkJson::decodeFromStream)

}