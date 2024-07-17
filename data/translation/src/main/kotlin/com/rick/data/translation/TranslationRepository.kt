package com.rick.data.translation

import kotlinx.coroutines.flow.Flow

interface TranslationRepository {

    fun getTranslation(body: TranslationBody): Flow<List<Translation>>
}