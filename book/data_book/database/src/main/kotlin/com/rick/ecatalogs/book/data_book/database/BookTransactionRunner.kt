package com.rick.ecatalogs.book.data_book.database

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