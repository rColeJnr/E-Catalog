package com.rick.data.translation

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    private val api: TranslationDataSource,
) : TranslationRepository {
    override fun getTranslation(body: TranslationBody): Flow<List<Translation>> =
        channelFlow {
            try {
                val response = api.getTranslation(body)

                send(response.translations)
            } catch (e: HttpException) {
                Log.e(TAG, e.localizedMessage ?: "Http exception")
            }
        }
}

private const val TAG = "Translation"