package com.rick.ecatalogs.book.data_book.domain

import com.rick.ecatalogs.data.translation.Translation
import com.rick.ecatalogs.data.translation.TranslationBody
import com.rick.ecatalogs.data.translation.TranslationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTranslationUseCase @Inject constructor(
    private val translationRepository: TranslationRepository
) {
    operator fun invoke(body: TranslationBody): Flow<List<Translation>> =
        translationRepository.getTranslation(body)
}