package com.rick.ecatalogs.ui

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import com.rick.ecatalogs.MainActivity
import com.rick.ecatalogs.auth.TestAuthSession
import com.rick.ecatalogs.data.ui_components.auth.TAG_FORGOT_PASSWORD
import com.rick.ecatalogs.data.ui_components.auth.TAG_MODE_CREATE
import com.rick.ecatalogs.data.ui_components.auth.TAG_MODE_SIGN_IN
import com.rick.ecatalogs.data.ui_components.auth.TAG_SUBMIT
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import com.rick.ecatalogs.authentication.auth_screen.R as AuthR
import com.rick.ecatalogs.data.ui_components.auth.R as LoginR

/**
 * The login screen, signed out. Only the checks made before Firebase is called are tested:
 * real sign-in, sign-up, Google and the reset email need a Firebase backend.
 */
@HiltAndroidTest
class LoginScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val notificationPermission = grantNotificationPermission()

    @get:Rule(order = 2)
    val composeRule = createEmptyComposeRule()

    @Inject
    lateinit var authSession: TestAuthSession

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltRule.inject()
        authSession.isSignedIn = false
        useTestWorkManager()
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() = scenario.close()

    // 1. Create mode first; the Sign in / Create account switch changes the form

    @Test
    fun whenOpened_thenCreateAccountModeIsShown() {
        tag(TAG_MODE_CREATE).assertIsSelected()
        tag(TAG_SUBMIT).assert(hasText(string(LoginR.string.data_ui_components_auth_create_account)))
        field(LoginR.string.data_ui_components_auth_email).assertIsDisplayed()
        field(LoginR.string.data_ui_components_auth_password).assertIsDisplayed()
        field(LoginR.string.data_ui_components_auth_username).assertIsDisplayed()
        tag(TAG_FORGOT_PASSWORD).assertDoesNotExist()
    }

    @Test
    fun createMode_whenSignInSelected_thenSignInModeIsShown() {
        switchToSignIn()

        tag(TAG_MODE_SIGN_IN).assertIsSelected()
        tag(TAG_SUBMIT).assert(hasText(string(LoginR.string.data_ui_components_auth_sign_in)))
        tag(TAG_FORGOT_PASSWORD).assertIsDisplayed()
        field(LoginR.string.data_ui_components_auth_email).assertIsDisplayed()
        field(LoginR.string.data_ui_components_auth_password).assertIsDisplayed()
        assertNoField(LoginR.string.data_ui_components_auth_username)
    }

    @Test
    fun signInMode_whenCreateAccountSelected_thenCreateModeIsShown() {
        switchToSignIn()
        tag(TAG_MODE_CREATE).performClick()

        tag(TAG_MODE_CREATE).assertIsSelected()
        field(LoginR.string.data_ui_components_auth_username).assertIsDisplayed()
    }

    // 2. Password tips, and the confirm field once the password is valid

    @Test
    fun createMode_whenPasswordTooShort_thenPasswordTipsAreShown() {
        field(LoginR.string.data_ui_components_auth_password).performTextInput("abc")

        composeRule.onNodeWithText(string(LoginR.string.data_ui_components_auth_a_chars_min), substring = true)
            .assertIsDisplayed()
        assertNoField(LoginR.string.data_ui_components_auth_confirm_password)
    }

    @Test
    fun createMode_whenPasswordValid_thenConfirmPasswordIsShownAndTipsHidden() {
        field(LoginR.string.data_ui_components_auth_password).performTextInput("secret12")

        field(LoginR.string.data_ui_components_auth_confirm_password).assertIsDisplayed()
        composeRule.onNodeWithText(string(LoginR.string.data_ui_components_auth_a_chars_min), substring = true)
            .assertDoesNotExist()
    }

    // 3. Username tips

    @Test
    fun createMode_whenUsernameTooShort_thenUsernameTipsAreShown() {
        field(LoginR.string.data_ui_components_auth_username).performTextInput("ab")

        composeRule.onNodeWithText(string(LoginR.string.data_ui_components_auth_username_chars), substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun createMode_whenUsernameValid_thenUsernameTipsAreHidden() {
        field(LoginR.string.data_ui_components_auth_username).performTextInput("rick")

        composeRule.onNodeWithText(string(LoginR.string.data_ui_components_auth_username_chars), substring = true)
            .assertDoesNotExist()
    }

    // 4. Sign in with empty fields

    @Test
    fun signInMode_whenSignInTappedWithEmptyFields_thenFillAllFieldsErrorIsShown() {
        switchToSignIn()
        tag(TAG_SUBMIT).performClick()

        node(AuthR.string.authentication_auth_screen_fill_all_fields).assertIsDisplayed()
    }

    // 5. Create with invalid input

    @Test
    fun createMode_whenCreateTappedWithInvalidInput_thenFailedToCreateErrorIsShown() {
        field(LoginR.string.data_ui_components_auth_email).performTextInput("not-an-email")
        field(LoginR.string.data_ui_components_auth_password).performTextInput("abc")
        field(LoginR.string.data_ui_components_auth_username).performTextInput("ab")
        tag(TAG_SUBMIT).performClick()

        node(AuthR.string.authentication_auth_screen_failed_to_create_account).assertIsDisplayed()
    }

    // 6. Forgot password dialog

    @Test
    fun signInMode_whenForgotPasswordTapped_thenResetDialogOpensAndDismissClosesIt() {
        switchToSignIn()
        tag(TAG_FORGOT_PASSWORD).performClick()
        node(LoginR.string.data_ui_components_auth_request_password_reset).assertIsDisplayed()

        node(LoginR.string.data_ui_components_auth_dismiss).performClick()
        assertNotShown(LoginR.string.data_ui_components_auth_request_password_reset)
    }

    private fun switchToSignIn() {
        tag(TAG_MODE_SIGN_IN).performClick()
    }

    private fun tag(tag: String) = composeRule.onNodeWithTag(tag)

    private fun node(id: Int) = composeRule.onNodeWithText(string(id))

    private fun assertNotShown(id: Int) = node(id).assertDoesNotExist()

    private fun field(label: Int) =
        composeRule.onNode(hasSetTextAction() and hasText(string(label)))

    private fun assertNoField(label: Int) = field(label).assertDoesNotExist()
}
