package com.rick.ui_components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rick.data.ui_components.auth.R

@Composable
fun PasswordResetDialog(
    onPasswordReset: (String) -> Unit,
    isValidEmail: (String) -> Boolean,
    onDismissRequest: () -> Unit
) {
    val email = rememberSaveable {
        mutableStateOf("")
    }
    val invalidEmail = rememberSaveable {
        mutableStateOf(false)
    }
    val container = colorResource(id = R.color.data_ui_components_auth_background)
    val content = colorResource(id = R.color.data_ui_components_auth_text)
    val button = colorResource(id = R.color.data_ui_components_auth_onBackground)

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = container, contentColor = content)
        ) {
            Column {
                Text(
                    stringResource(R.string.data_ui_components_auth_request_password_reset),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp, vertical = 8.dp)
                )
                Text(
                    stringResource(R.string.data_ui_components_auth_enter_the_email_associated),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp, vertical = 8.dp)
                )
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.data_ui_components_auth_email_placeholder)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                AnimatedVisibility(visible = invalidEmail.value) {
                    Text(
                        text = stringResource(R.string.data_ui_components_auth_invalid_email),
                        color = Color.Red,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .padding(6.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(color = button)
                    ) {
                        Text(
                            text = stringResource(R.string.data_ui_components_auth_dismiss),
                            color = content
                        )
                    }
                    TextButton(
                        onClick = {
                            if (isValidEmail(email.value)) {
                                onPasswordReset(email.value)
                                onDismissRequest()
                            } else {
                                invalidEmail.value = true
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(shape = RoundedCornerShape(16.dp))
                            .background(color = button)
                    ) {
                        Text(
                            text = stringResource(R.string.data_ui_components_auth_send),
                            color = content
                        )
                    }
                }
            }
        }
    }

}

@Preview
@Composable
fun PreviewPasswordResetDialog() {
    PasswordResetDialog(onPasswordReset = {}, isValidEmail = { true }, onDismissRequest = {})
}
