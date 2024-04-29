package com.rick.data.ui_components.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

suspend fun EcsSnackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    undoRemoval: () -> Unit,
    clearUndoState: () -> Unit
) {
    val snackBarResult = snackbarHostState.showSnackbar(
        message = message,
        actionLabel = "Undo",
        duration = SnackbarDuration.Long
    ) == SnackbarResult.ActionPerformed
    if (snackBarResult) {
        undoRemoval()
    } else {
        clearUndoState()
    }
}

private const val TAG = "EcsSnackbar"