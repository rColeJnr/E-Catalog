package com.rick.ecatalogs.settings.screen_settings

import androidx.compose.animation.AnimatedContent
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_MONTH_HOURS
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_OPTIONS_HOURS
import com.rick.ecatalogs.settings.data_settings.model.SYNC_INTERVAL_WEEK_HOURS
import com.rick.ecatalogs.settings.data_settings.model.SettingsUserData
import com.rick.ecatalogs.settings.data_settings.model.ThemeConfig
import com.rick.ecatalogs.settings.screen_settings.R.string

/** Callbacks from the settings screen, handled by [SettingsFragment]. */
class SettingsActions(
    val onSaveUserName: (String) -> Unit,
    val onThemeChange: (ThemeConfig) -> Unit,
    val onNotificationsChange: (NotificationCategory, Boolean) -> Unit,
    val onSyncEnabledChange: (Boolean) -> Unit,
    val onSyncIntervalChange: (Int) -> Unit,
    val onSignOut: () -> Unit,
    val onDeleteAccount: () -> Unit,
    val onOpenLicenses: () -> Unit,
)

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    appVersion: String,
    actions: SettingsActions,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = uiState,
        contentKey = { it is SettingsUiState.Success },
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "settingsState",
        modifier = modifier.fillMaxSize(),
    ) { state ->
        when (state) {
            SettingsUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }

            is SettingsUiState.Success -> SettingsContent(state.settings, appVersion, actions)
        }
    }
}

@Composable
private fun SettingsContent(
    settings: SettingsUserData,
    appVersion: String,
    actions: SettingsActions,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            // The app draws behind the navigation bar (edge to edge): without this the end of the
            // page (About: links and version) stays under it
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        AccountSection(settings.userName, actions)
        SectionDivider()
        NotificationsSection(settings.enabledNotifications, actions.onNotificationsChange)
        SectionDivider()
        SyncSection(settings, actions)
        SectionDivider()
        ThemeSection(settings.themeConfig, actions.onThemeChange)
        SectionDivider()
        AboutSection(appVersion, actions.onOpenLicenses)
    }
}

@Composable
private fun AccountSection(userName: String, actions: SettingsActions) {
    SectionTitle(stringResource(string.settings_screen_settings_account))
    var editing by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable(userName) { mutableStateOf(userName) }
    var confirmDelete by remember { mutableStateOf(false) }

    Text(
        text = stringResource(string.settings_screen_settings_greeting, userName),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 4.dp),
    )
    AnimatedVisibility(
        visible = editing,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text(stringResource(string.settings_screen_settings_username)) },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    actions.onSaveUserName(name)
                    editing = false
                },
            ) { Text(stringResource(string.settings_screen_settings_save)) }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        TextButton(onClick = { editing = !editing }) {
            Text(stringResource(string.settings_screen_settings_change_username))
        }
        TextButton(onClick = actions.onSignOut) {
            Text(stringResource(string.settings_screen_settings_sign_out))
        }
        TextButton(onClick = { confirmDelete = true }) {
            Text(
                stringResource(string.settings_screen_settings_delete_account),
                color = MaterialTheme.colorScheme.error,
            )
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(string.settings_screen_settings_delete_account)) },
            text = { Text(stringResource(string.settings_screen_settings_delete_account_message)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    actions.onDeleteAccount()
                }) {
                    Text(
                        stringResource(string.settings_screen_settings_delete),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(string.settings_screen_settings_cancel))
                }
            },
        )
    }
}

@Composable
private fun NotificationsSection(
    enabled: Set<NotificationCategory>,
    onChange: (NotificationCategory, Boolean) -> Unit,
) {
    SectionTitle(stringResource(string.settings_screen_settings_notifications))
    NotificationCategory.entries.forEach { category ->
        SwitchRow(
            text = stringResource(category.labelRes),
            checked = category in enabled,
            onCheckedChange = { onChange(category, it) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SyncSection(settings: SettingsUserData, actions: SettingsActions) {
    SectionTitle(stringResource(string.settings_screen_settings_background_sync))
    SwitchRow(
        text = stringResource(string.settings_screen_settings_sync_enabled),
        supportingText = stringResource(string.settings_screen_settings_sync_description),
        checked = settings.syncEnabled,
        onCheckedChange = actions.onSyncEnabledChange,
    )
    AnimatedVisibility(
        visible = settings.syncEnabled,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
    ) {
        Column {
            Text(
                text = stringResource(string.settings_screen_settings_sync_interval),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SYNC_INTERVAL_OPTIONS_HOURS.forEach { hours ->
                    FilterChip(
                        selected = settings.syncIntervalHours == hours,
                        onClick = { actions.onSyncIntervalChange(hours) },
                        label = { Text(syncIntervalLabel(hours)) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeSection(themeConfig: ThemeConfig, onChange: (ThemeConfig) -> Unit) {
    SectionTitle(stringResource(string.settings_screen_settings_theme))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemeConfig.entries.forEach { option ->
            FilterChip(
                selected = themeConfig == option,
                onClick = { onChange(option) },
                label = { Text(stringResource(option.labelRes)) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AboutSection(appVersion: String, onOpenLicenses: () -> Unit) {
    SectionTitle(stringResource(string.settings_screen_settings_about))
    val uriHandler = LocalUriHandler.current
    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        TextButton(onClick = { uriHandler.openUri(PRIVACY_POLICY_URL) }) {
            Text(stringResource(string.settings_screen_settings_privacy_policy))
        }
        TextButton(onClick = { uriHandler.openUri(TERMS_OF_USE_URL) }) {
            Text(stringResource(string.settings_screen_settings_terms_of_use))
        }
        TextButton(onClick = onOpenLicenses) {
            Text(stringResource(string.settings_screen_settings_licenses))
        }
    }
    Text(
        text = stringResource(string.settings_screen_settings_version, appVersion),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun SwitchRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    supportingText: String? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 8.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(text, style = MaterialTheme.typography.bodyLarge)
            if (supportingText != null) {
                Text(
                    supportingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        // The row handles the toggle so the whole line is tappable
        Switch(checked = checked, onCheckedChange = null)
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun SectionDivider() = HorizontalDivider(Modifier.padding(top = 12.dp))

private val NotificationCategory.labelRes: Int
    get() = when (this) {
        NotificationCategory.TRENDING_MOVIES -> string.settings_screen_settings_trending_movies
        NotificationCategory.TRENDING_SERIES -> string.settings_screen_settings_trending_series
        NotificationCategory.ARTICLES -> string.settings_screen_settings_movie_reviews
        NotificationCategory.BESTSELLERS -> string.settings_screen_settings_bestsellers
        NotificationCategory.GAMES -> string.settings_screen_settings_games
        NotificationCategory.ANIME -> string.settings_screen_settings_anime
        NotificationCategory.MANGA -> string.settings_screen_settings_manga
    }

private val ThemeConfig.labelRes: Int
    get() = when (this) {
        ThemeConfig.FOLLOW_SYSTEM -> string.settings_screen_settings_theme_system
        ThemeConfig.LIGHT -> string.settings_screen_settings_theme_light
        ThemeConfig.DARK -> string.settings_screen_settings_theme_dark
    }

@Preview
@Composable
private fun PreviewSettingsScreen() {
    EcsTheme {
        SettingsScreen(
            uiState = SettingsUiState.Success(SettingsUserData(userName = "rColeJnr")),
            appVersion = "2.1",
            actions = SettingsActions({}, {}, { _, _ -> }, {}, {}, {}, {}, {}),
        )
    }
}

private const val PRIVACY_POLICY_URL =
    "https://doc-hosting.flycricket.io/e-catalogs-privacy-policy/b84b04b6-5143-43d8-ae9f-5927719e1914/privacy"
private const val TERMS_OF_USE_URL =
    "https://doc-hosting.flycricket.io/e-catalogs-terms-of-use/c48319d6-7440-4240-b546-12bab7f55d9c/terms"

@Composable
private fun syncIntervalLabel(hours: Int): String = when (hours) {
    SYNC_INTERVAL_WEEK_HOURS -> stringResource(string.settings_screen_settings_sync_weekly)
    SYNC_INTERVAL_MONTH_HOURS -> stringResource(string.settings_screen_settings_sync_monthly)
    else -> stringResource(string.settings_screen_settings_hours, hours)
}
