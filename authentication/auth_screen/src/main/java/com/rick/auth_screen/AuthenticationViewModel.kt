package com.rick.auth_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.settings.data_settings.data.repository.UserSettingsDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val userSettingsDataRepository: UserSettingsDataRepository,
    private val savedSteHandle: SavedStateHandle
) : ViewModel() {

    // Email
    private val email = savedSteHandle.getStateFlow(key = EMAIL_QUERY, initialValue = "")
    private val isValidEmail = MutableStateFlow(false)

    // Password
    private val password = MutableStateFlow("")
    private val confirmPassword = MutableStateFlow("")
    private val passwordVisible = MutableStateFlow(true)
    private val showPasswordTips = MutableStateFlow(false)
    private val showConfirmPassword = MutableStateFlow(false)

    // Username
    private val username = savedSteHandle.getStateFlow(key = USERNAME_QUERY, initialValue = "")
    private val isValidUsername = MutableStateFlow(false)
    private val showUsernameTips = MutableStateFlow(false)

    // Screen State
    private val progressState = MutableStateFlow(false)
    private val authErrorMsg = MutableStateFlow("")

    /* True if create account state, false if sign in state */
    private val authScreenState =
        savedSteHandle.getStateFlow(key = SCREEN_STATE_QUERY, initialValue = true)

    // StateFlow
    private val _screenState = MutableStateFlow(AuthScreenState())
    val screenState: StateFlow<AuthScreenState>
        get() = _screenState

    private val _inputState = MutableStateFlow(InputState())
    val inputState: StateFlow<InputState>
        get() = _inputState

    private val _passwordState = MutableStateFlow(PasswordState())
    val passwordState: StateFlow<PasswordState>
        get() = _passwordState

    init {

        viewModelScope.launch {
            combine(
                progressState,
                authErrorMsg,
                authScreenState
            ) { progress, msg, screen ->
                AuthScreenState(
                    authScreenState = screen,
                    progressState = progress,
                    authErrorMsg = msg
                )
            }.catch {
                authErrorMsg.value = it.message ?: ""
            }.collect {
                _screenState.value = it
            }
        }

        viewModelScope.launch {
            combine(
                email,
                isValidEmail,
                username,
                isValidUsername,
                showUsernameTips
            ) { email, isValidEmail, username, isValidUsername, showUsernameTips ->
                InputState(
                    email = email,
                    isValidEmail = isValidEmail,
                    username = username,
                    isValidUsername = isValidUsername,
                    showUsernameTips = showUsernameTips
                )
            }.catch {
                authErrorMsg.value = it.message ?: ""
            }.collect {
                _inputState.value = it
            }
        }

        viewModelScope.launch {
            combine(
                password,
                confirmPassword,
                passwordVisible,
                showPasswordTips,
                showConfirmPassword
            ) { password, confirmPassword, passwordVisible, showPasswordTips, showConfirmPassword ->
                PasswordState(
                    password = password,
                    confirmPassword = confirmPassword,
                    passwordVisible = passwordVisible,
                    showPasswordTips = showPasswordTips,
                    showConfirmPassword = showConfirmPassword
                )
            }.catch {
                authErrorMsg.value = it.message ?: ""
            }.collect {
                _passwordState.value = it
            }
        }
    }

    fun onEmailValueChange(email: String) {
        savedSteHandle[EMAIL_QUERY] = email
        this.isValidEmail.value = isValidEmail(email)
    }

    fun isEmailInputValid(email: String): Boolean =
        isValidEmail(email)

    fun onPasswordValueChange(password: String) {
        this.password.value = password
        val validPass = isValidPassword(password)
        this.showPasswordTips.value = !validPass
        this.showConfirmPassword.value = validPass
    }

    fun onConfirmPasswordValueChange(password: String) {
        this.confirmPassword.value = password
    }

    fun onPasswordVisible() {
        this.passwordVisible.value = !this.passwordVisible.value
    }

    fun onUsernameValueChange(username: String) {
        savedSteHandle[USERNAME_QUERY] = username
        val validName = isValidUsername(username)
        showUsernameTips.value = !validName
        isValidUsername.value = validName
    }

    fun onScreenStateCreateValueChange() {
        savedSteHandle[SCREEN_STATE_QUERY] = !authScreenState.value
    }

    fun showProgressState(state: Boolean) {
        progressState.value = state
    }

    fun saveUsernameToDatastore(username: String) {
        viewModelScope.launch {
            userSettingsDataRepository.setUserName(username)
        }
    }

    fun onAuthErrorMsg(msg: String) {
        this.authErrorMsg.value = msg
    }
}

private const val EMAIL_QUERY = "emailQuery"
private const val USERNAME_QUERY = "usernameQuery"
private const val SCREEN_STATE_QUERY = "screenStateQuery"