package com.rick.settings.screen_settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.rick.data.ui_components.common.EcsAnimatedVisibilityBox
import com.rick.data.ui_components.common.EcsText
import com.rick.data.ui_components.common.EcsTextButton
import com.rick.settings.screen_settings.R.string

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    showUsernameTextField: Boolean,
    shouldShowUsernameTextField: () -> Unit,
    onSaveUsername: () -> Unit,
    shouldShowDeleteButton: () -> Unit,
    showDeleteButton: Boolean,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit
) {
    val configuration = LocalConfiguration.current

    /**
     * usePlatformDefaultWidth = false is use as a temporary fix to allow
     * height recalculation during recomposition. This, however, causes
     * Dialog's to occupy full width in Compact mode. Therefore max width
     * is configured below. This should be removed when there's fix to
     * https://issuetracker.google.com/issues/221643630
     */
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.widthIn(max = configuration.screenWidthDp.dp - 80.dp),
        containerColor = colorResource(id = com.rick.data.ui_components.common.R.color.data_ui_components_common_background),
        onDismissRequest = { onDismiss() },
        title = {
            EcsText(
                text = stringResource(string.settings_screen_settings_settings), fontSize = 30.sp
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                EcsText(
                    modifier = Modifier.fillMaxWidth(), text = "Hi, $username"
                )
                HorizontalDivider(Modifier.padding(bottom = 8.dp))
                OptionsPanel(
                    username = username,
                    onUsernameChange = onUsernameChange,
                    shouldShowUsernameTextField = shouldShowUsernameTextField,
                    showUsernameTextField = showUsernameTextField,
                    onSaveUsername = onSaveUsername,
                    shouldShowDeleteButton = shouldShowDeleteButton,
                    showDeleteButton = showDeleteButton,
                    onDeleteAccount = onDeleteAccount,
                    onSignOut = onSignOut
                )
                HorizontalDivider(Modifier.padding(top = 8.dp))
                LinksPanel()
            }
        },
        confirmButton = {
            EcsText(
                text = stringResource(string.settings_screen_settings_ok),
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .clickable { onDismiss() },
            )
        },
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OptionsPanel(
    username: String = "Username",
    onUsernameChange: (String) -> Unit,
    showUsernameTextField: Boolean,
    shouldShowUsernameTextField: () -> Unit,
    onSaveUsername: () -> Unit,
    shouldShowDeleteButton: () -> Unit,
    showDeleteButton: Boolean,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit
) {
    FlowColumn(
        verticalArrangement = Arrangement.spacedBy(
            space = (-4).dp, alignment = Alignment.CenterVertically
        ), modifier = Modifier.fillMaxWidth()
    ) {
        EcsTextButton(
            onClick = { shouldShowUsernameTextField() }, text = "Change my username"
        )
        val density = LocalDensity.current
        EcsAnimatedVisibilityBox(
            screenState = showUsernameTextField, density = density, modifier = Modifier
        ) {
            Column(
                Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End
            ) {
                TextField(
                    value = username,
                    onValueChange = { onUsernameChange(it) },
                    placeholder = {
                        Text(
                            text = "Ricardo"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true,

                    )
                Spacer(modifier = Modifier.height(2.dp))
                EcsTextButton(
                    text = "Save",
                    onClick = onSaveUsername
                )
            }
        }
        EcsTextButton(
            onClick = onSignOut, text = "Sign out"
        )
        EcsTextButton(
            onClick = shouldShowDeleteButton,
            text = "Delete my account"
        )
        EcsAnimatedVisibilityBox(
            screenState = showDeleteButton,
            density = density,
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                EcsTextButton(
                    modifier = Modifier,
                    onClick = onDeleteAccount,
                    text = "Confirm",
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LinksPanel() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        val uriHandler = LocalUriHandler.current
        EcsTextButton(
            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }, text = "Privacy policy"
        )
        EcsTextButton(
            onClick = { uriHandler.openUri(TERMS_OF_USE_URL) }, text = "Terms of use"
        )
        val context = LocalContext.current
        EcsTextButton(
            onClick = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            }, text = "Licenses"
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsDialog() {
    MaterialTheme {
        SettingsDialog(
            onDismiss = {},
            onDeleteAccount = {},
            onSignOut = {},
            onUsernameChange = {},
            shouldShowUsernameTextField = { },
            showUsernameTextField = true,
            onSaveUsername = {},
            shouldShowDeleteButton = {},
            showDeleteButton = true,
            username = "rColeJnr"
        )
    }
}

private const val PRIVACY_POLICY_URL =
    "https://doc-hosting.flycricket.io/e-catalogs-privacy-policy/b84b04b6-5143-43d8-ae9f-5927719e1914/privacy"
private const val TERMS_OF_USE_URL =
    "https://doc-hosting.flycricket.io/e-catalogs-terms-of-use/c48319d6-7440-4240-b546-12bab7f55d9c/terms"
