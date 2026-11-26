package com.rick.ecatalogs.game.data_game.database

import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameTransactionRunner @Inject constructor(
    private val database: GameDatabase,
) {
    suspend operator fun <R> invoke(block: suspend () -> R): R = database.withTransaction(block)
}
