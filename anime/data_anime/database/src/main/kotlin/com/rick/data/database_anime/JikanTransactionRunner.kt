package com.rick.data.database_anime

import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JikanTransactionRunner @Inject constructor(
    private val database: JikanDatabase,
) {
    suspend operator fun <R> invoke(block: suspend () -> R): R =
        database.withTransaction(block)
}