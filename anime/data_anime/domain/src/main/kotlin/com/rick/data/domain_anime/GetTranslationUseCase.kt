package com.rick.data.domain_anime

import com.rick.data.translation.Translation
import com.rick.data.translation.TranslationBody
import com.rick.data.translation.TranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTranslationUseCase @Inject constructor(
    private val translationRepository: TranslationRepository
) {
    operator fun invoke(body: TranslationBody): Flow<List<Translation>> =
        translationRepository.getTranslation(body)
}