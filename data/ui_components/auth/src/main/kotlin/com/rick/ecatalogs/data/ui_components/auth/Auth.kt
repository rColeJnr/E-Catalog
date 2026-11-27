package com.rick.ecatalogs.data.ui_components.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp

/**
 * Sign in / create account. Stateless: the state and the Firebase calls live in
 * AuthenticationViewModel / AuthenticationFragment. The fragment wraps it in EcsTheme.
 *
 * Note: [passwordVisible] true means the password is masked (the ViewModel's naming).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    email: String,
    onEmailValueChange: (String) -> Unit,
    isValidEmail: Boolean,
    isEmailInputValid: (String) -> Boolean,
    password: String,
    onPasswordValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisible: () -> Unit,
    showPasswordTips: Boolean,
    showConfirmPassword: Boolean,
    confirmPassword: String,
    onConfirmPasswordValueChange: (String) -> Unit,
    isValidConfirmPassword: Boolean,
    username: String,
    onUsernameValueChange: (String) -> Unit,
    isValidUsername: Boolean,
    showUsernameTips: Boolean,
    screenStateCreate: Boolean,
    onScreenStateCreateValueChange: () -> Unit,
    progressState: Boolean,
    showProgressState: (Boolean) -> Unit,
    onGoogleOneTap: () -> Unit,
    onAuthenticate: (email: String, password: String) -> Unit,
    onCreateAccount: (email: String, password: String, username: String) -> Unit,
    onResetPassword: (String) -> Unit,
    authErrorMsg: String
) {
    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val submit = {
        keyboard?.hide()
        showProgressState(true)
        if (screenStateCreate) onCreateAccount(email, password, username)
        else onAuthenticate(email, password)
    }

    BoxWithConstraints(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        val density = LocalDensity.current
        var headerHeight by remember { mutableStateOf(0.dp) }
        val verticalPadding = 32.dp
        val headerGap = 28.dp
        val screenHeight = maxHeight
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = verticalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BrandHeader(
                Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .onSizeChanged { headerHeight = with(density) { it.height.toDp() } }
            )
            Spacer(Modifier.height(headerGap))

            Box(
                Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .heightIn(
                        min = (screenHeight - verticalPadding * 2 - headerHeight - headerGap)
                            .coerceAtLeast(0.dp)
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    border = CardDefaults.outlinedCardBorder(),
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ModeSwitch(
                            createSelected = screenStateCreate,
                            onSelect = { create ->
                                if (create != screenStateCreate) {
                                    showProgressState(false)
                                    onScreenStateCreateValueChange()
                                }
                            },
                        )

                        AnimatedVisibility(
                            visible = authErrorMsg.isNotBlank(),
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut(),
                        ) {
                            ErrorBanner(
                                stringResource(R.string.data_ui_components_auth_failed_to_authenticate, authErrorMsg)
                            )
                        }

                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailValueChange,
                            label = { Text(stringResource(R.string.data_ui_components_auth_email)) },
                            placeholder = { Text(stringResource(R.string.data_ui_components_auth_email_placeholder)) },
                            trailingIcon = { if (email.isNotEmpty()) ValidityIcon(isValidEmail) },
                            isError = email.isNotEmpty() && !isValidEmail,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                        )

                        PasswordField(
                            value = password,
                            onValueChange = onPasswordValueChange,
                            label = stringResource(R.string.data_ui_components_auth_password),
                            masked = passwordVisible,
                            onToggleMask = onPasswordVisible,
                            imeAction = if (screenStateCreate) ImeAction.Next else ImeAction.Done,
                            onDone = submit,
                            isError = false,
                            supportingText = if (screenStateCreate && showPasswordTips) {
                                {
                                    BulletList(
                                        listOf(
                                            stringResource(R.string.data_ui_components_auth_no_whitespaces),
                                            stringResource(R.string.data_ui_components_auth_a_chars_min),
                                            stringResource(R.string.data_ui_components_auth_latino_alphabet),
                                        )
                                    )
                                }
                            } else null,
                        )

                        AnimatedVisibility(visible = !screenStateCreate) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                TextButton(
                                    onClick = { showResetDialog = true },
                                    modifier = Modifier.testTag(TAG_FORGOT_PASSWORD),
                                ) {
                                    Text(stringResource(R.string.data_ui_components_auth_forgot_password))
                                }
                            }
                        }

                        AnimatedVisibility(visible = screenStateCreate && showConfirmPassword) {
                            val mismatch = confirmPassword.isNotEmpty() && !isValidConfirmPassword
                            PasswordField(
                                value = confirmPassword,
                                onValueChange = onConfirmPasswordValueChange,
                                label = stringResource(R.string.data_ui_components_auth_confirm_password),
                                masked = passwordVisible,
                                onToggleMask = onPasswordVisible,
                                imeAction = ImeAction.Next,
                                onDone = {},
                                isError = mismatch,
                                supportingText = if (mismatch) {
                                    { Text(stringResource(R.string.data_ui_components_auth_passwords_dont_match)) }
                                } else null,
                            )
                        }

                        AnimatedVisibility(visible = screenStateCreate) {
                            OutlinedTextField(
                                value = username,
                                onValueChange = onUsernameValueChange,
                                label = { Text(stringResource(R.string.data_ui_components_auth_username)) },
                                trailingIcon = { if (username.isNotEmpty()) ValidityIcon(isValidUsername) },
                                isError = username.isNotEmpty() && !isValidUsername,
                                supportingText = if (showUsernameTips) {
                                    {
                                        BulletList(
                                            listOf(
                                                stringResource(R.string.data_ui_components_auth_no_whitespaces),
                                                stringResource(R.string.data_ui_components_auth_username_chars),
                                            )
                                        )
                                    }
                                } else null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { submit() }),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }

                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = submit,
                            enabled = !progressState,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag(TAG_SUBMIT),
                        ) {
                            if (progressState) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                Text(
                                    stringResource(
                                        if (screenStateCreate) R.string.data_ui_components_auth_create_account
                                        else R.string.data_ui_components_auth_sign_in
                                    ),
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }

                        OrDivider()

                        OutlinedButton(
                            onClick = {
                                showProgressState(true)
                                onGoogleOneTap()
                            },
                            enabled = !progressState,
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag(TAG_GOOGLE),
                        ) {
                            Image(
                                painterResource(R.drawable.data_ui_components_auth_google_icon),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(stringResource(R.string.data_ui_components_auth_continue_with_google))
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        PasswordResetDialog(
            onPasswordReset = onResetPassword,
            isValidEmail = isEmailInputValid,
            onDismissRequest = { showResetDialog = false },
        )
    }
}

/** App icon and "Catalogs" in the brand serif, then the slogan. */
@Composable
private fun BrandHeader(modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(com.rick.ecatalogs.data.ui_design.R.drawable.data_ui_design_app_icon),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier.size(44.dp),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                stringResource(R.string.data_ui_components_auth_catalogs),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            stringResource(R.string.data_ui_components_auth_slogan),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModeSwitch(createSelected: Boolean, onSelect: (create: Boolean) -> Unit) {
    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
        SegmentedButton(
            selected = !createSelected,
            onClick = { onSelect(false) },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            modifier = Modifier.testTag(TAG_MODE_SIGN_IN),
        ) { Text(stringResource(R.string.data_ui_components_auth_sign_in)) }
        SegmentedButton(
            selected = createSelected,
            onClick = { onSelect(true) },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            modifier = Modifier.testTag(TAG_MODE_CREATE),
        ) { Text(stringResource(R.string.data_ui_components_auth_create_account)) }
    }
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    masked: Boolean,
    onToggleMask: () -> Unit,
    imeAction: ImeAction,
    onDone: () -> Unit,
    isError: Boolean,
    supportingText: (@Composable () -> Unit)?,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(stringResource(R.string.data_ui_components_auth_password_placeholder)) },
        visualTransformation = if (masked) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = {
            IconButton(onClick = onToggleMask) {
                Icon(
                    if (masked) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = stringResource(
                        if (masked) R.string.data_ui_components_auth_show_password
                        else R.string.data_ui_components_auth_hide_password
                    ),
                )
            }
        },
        isError = isError,
        supportingText = supportingText,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun ValidityIcon(valid: Boolean) {
    Icon(
        if (valid) Icons.Outlined.CheckCircle else Icons.Outlined.ErrorOutline,
        contentDescription = stringResource(
            if (valid) R.string.data_ui_components_auth_valid_input
            else R.string.data_ui_components_auth_invalid_input
        ),
        tint = if (valid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
    )
}

@Composable
private fun ErrorBanner(message: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun OrDivider() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            stringResource(R.string.data_ui_components_auth_or),
            modifier = Modifier.padding(horizontal = 12.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        HorizontalDivider(Modifier.weight(1f), color = MaterialTheme.colorScheme.outlineVariant)
    }
}

/** Test tags for the controls whose text appears twice (the mode switch and the submit button). */
const val TAG_MODE_SIGN_IN = "auth_mode_sign_in"
const val TAG_MODE_CREATE = "auth_mode_create"
const val TAG_SUBMIT = "auth_submit"
const val TAG_GOOGLE = "auth_google"
const val TAG_FORGOT_PASSWORD = "auth_forgot_password"

@Preview
@Composable
private fun PreviewLoginScreen() {
    LoginScreen(
        email = "", onEmailValueChange = {}, isValidEmail = false, isEmailInputValid = { true },
        password = "", onPasswordValueChange = {}, passwordVisible = true, onPasswordVisible = {},
        showPasswordTips = false, showConfirmPassword = false, confirmPassword = "",
        onConfirmPasswordValueChange = {}, isValidConfirmPassword = true, username = "",
        onUsernameValueChange = {}, isValidUsername = false, showUsernameTips = false,
        screenStateCreate = false, onScreenStateCreateValueChange = {}, progressState = false,
        showProgressState = {}, onGoogleOneTap = {}, onAuthenticate = { _, _ -> },
        onCreateAccount = { _, _, _ -> }, onResetPassword = {}, authErrorMsg = "",
    )
}
