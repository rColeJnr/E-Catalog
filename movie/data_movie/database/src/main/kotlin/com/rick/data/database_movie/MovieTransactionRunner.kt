package com.rick.data.database_movie

import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieTransactionRunner @Inject constructor(
    private val database: MovieDatabase,
) {
    suspend operator fun <R> invoke(block: suspend () -> R): R =
        database.withTransaction(block)
}