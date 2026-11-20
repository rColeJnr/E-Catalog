package com.rick.data.database_book

import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookTransactionRunner @Inject constructor(
    private val database: BookDatabase,
) {
    suspend operator fun <R> invoke(block: suspend () -> R): R =
        database.withTransaction(block)
}