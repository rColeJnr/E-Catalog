package com.rick.settings.screen_settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.rick.data.ui_components.common.EcsText
import com.rick.data.ui_components.common.EcsTextButton
import com.rick.settings.screen_settings.R.string

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    username: String,
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
        onDismissRequest = { onDismiss() },
        title = {
            EcsText(
                text = stringResource(string.settings_screen_settings_settings),
                fontSize = 30.sp
            )
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                HorizontalDivider(Modifier.padding(bottom = 8.dp))
                EcsText(
                    modifier = Modifier.fillMaxWidth(),
                    text = """Hi, $username
Thank you for downloading my app, it means a lot. 🤗"""
                )
                HorizontalDivider(Modifier.padding(top = 8.dp))
                LinksPanel()
            }
        },
        confirmButton = {
            Text(
                text = stringResource(string.settings_screen_settings_ok),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .clickable { onDismiss() },
            )
        },
    )
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
            onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) },
            text = "Privacy policy"
        )
        EcsTextButton(
            onClick = { uriHandler.openUri(TERMS_OF_USE_URL) },
            text = "Terms of use"
        )
        val context = LocalContext.current
        EcsTextButton(
            onClick = {
                context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
            },
            text = "Licenses"
        )
    }
}

@Preview
@Composable
private fun PreviewSettingsDialog() {
    MaterialTheme {
        SettingsDialog(
            onDismiss = {},
            username = "rColeJnr"
        )
    }
}

private const val PRIVACY_POLICY_URL =
    "https://doc-hosting.flycricket.io/e-catalogs-privacy-policy/b84b04b6-5143-43d8-ae9f-5927719e1914/privacy"
private const val TERMS_OF_USE_URL = "https://doc-hosting.flycricket.io/e-catalogs-terms-of-use/c48319d6-7440-4240-b546-12bab7f55d9c/terms"
