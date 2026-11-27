package com.rick.ecatalogs.data.ui_components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Asks for the account's email and sends the reset link (Firebase, in the fragment). */
@Composable
fun PasswordResetDialog(
    onPasswordReset: (String) -> Unit,
    isValidEmail: (String) -> Boolean,
    onDismissRequest: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var invalidEmail by rememberSaveable { mutableStateOf(false) }
    val send = {
        if (isValidEmail(email)) {
            onPasswordReset(email)
            onDismissRequest()
        } else {
            invalidEmail = true
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(28.dp),
        title = { Text(stringResource(R.string.data_ui_components_auth_request_password_reset)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.data_ui_components_auth_enter_the_email_associated),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        invalidEmail = false
                    },
                    label = { Text(stringResource(R.string.data_ui_components_auth_email)) },
                    placeholder = { Text(stringResource(R.string.data_ui_components_auth_email_placeholder)) },
                    isError = invalidEmail,
                    supportingText = if (invalidEmail) {
                        {
                            Text(
                                stringResource(R.string.data_ui_components_auth_invalid_email)
                                    .trimStart('\t', '•', ' ')
                            )
                        }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { send() }),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = send) { Text(stringResource(R.string.data_ui_components_auth_send)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.data_ui_components_auth_dismiss))
            }
        },
    )
}

@Preview
@Composable
private fun PreviewPasswordResetDialog() {
    PasswordResetDialog(onPasswordReset = {}, isValidEmail = { true }, onDismissRequest = {})
}
