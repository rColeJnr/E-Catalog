package com.rick.data.translation

interface TranslationDataSource {

    suspend fun getTranslation(
        body: TranslationBody
    ): TranslationResponse

}