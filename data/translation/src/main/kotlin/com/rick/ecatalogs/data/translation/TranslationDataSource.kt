package com.rick.ecatalogs.data.translation

interface TranslationDataSource {

    suspend fun getTranslation(
        body: TranslationBody
    ): TranslationResponse

}