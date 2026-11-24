package com.rick.ecatalogs.settings.screen_settings

import android.Manifest
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseAuth
import com.rick.ecatalogs.settings.data_settings.model.NotificationCategory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModels()

    private var pendingNotificationCategory: NotificationCategory? = null

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val category = pendingNotificationCategory ?: return@registerForActivityResult
        pendingNotificationCategory = null
        if (granted) {
            viewModel.setNotificationsEnabled(category, true)
        } else {
            Toast.makeText(
                requireContext(),
                R.string.settings_screen_settings_notifications_denied,
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            EcsTheme {
                SettingsScreen(
                    uiState = uiState,
                    appVersion = appVersion(),
                    actions = SettingsActions(
                        onSaveUserName = viewModel::setUserName,
                        onThemeChange = viewModel::setThemeConfig,
                        onNotificationsChange = ::onNotificationsChange,
                        onSyncEnabledChange = viewModel::setSyncEnabled,
                        onSyncIntervalChange = viewModel::setSyncIntervalHours,
                        onSignOut = ::signOut,
                        onDeleteAccount = ::deleteAccount,
                        onOpenLicenses = {
                            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
                        },
                    ),
                )
            }
        }
    }

    private fun onNotificationsChange(category: NotificationCategory, enabled: Boolean) {
        val needsPermission = enabled &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) {
            pendingNotificationCategory = category
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.setNotificationsEnabled(category, enabled)
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val navController = findNavController()
        navController.navigate(
            NavDeepLinkRequest.Builder.fromUri(Uri.parse(AUTHENTICATION_DEEP_LINK)).build(),
            navOptions { popUpTo(navController.graph.id) { inclusive = true } },
        )
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser ?: return signOut()
        user.delete().addOnCompleteListener {
            if (!isAdded) return@addOnCompleteListener
            Toast.makeText(
                requireContext(), R.string.settings_screen_settings_account_deleted, Toast.LENGTH_LONG
            ).show()
            signOut()
        }
    }

    private fun appVersion(): String = runCatching {
        requireContext().packageManager
            .getPackageInfo(requireContext().packageName, 0)
            .versionName
    }.getOrNull().orEmpty()
}

private const val AUTHENTICATION_DEEP_LINK = "com.rick.ecs://authentication_fragment"
