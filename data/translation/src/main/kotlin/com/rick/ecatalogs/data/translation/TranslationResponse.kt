package com.rick.ecatalogs.data.translation

import kotlinx.serialization.Serializable

@Serializable
data class TranslationResponse(
    val translations: List<Translation>
)

