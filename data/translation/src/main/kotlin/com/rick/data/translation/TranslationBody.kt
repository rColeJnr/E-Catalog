package com.rick.data.translation

data class TranslationBody(
    val folderId: String,
    val targetLanguageCode: String,
    val texts: List<String>
)