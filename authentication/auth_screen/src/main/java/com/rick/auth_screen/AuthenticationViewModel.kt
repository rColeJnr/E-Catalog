package com.rick.auth_screen

import androidx.compose.runtime.mutableStateOf
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

    private val email = savedSteHandle.getStateFlow(key = EMAIL_QUERY, initialValue = "")
    private val username = savedSteHandle.getStateFlow(key = USERNAME_QUERY, initialValue = "")
    val passwordVisible =
        savedSteHandle.getStateFlow(key = PASSWORD_VISIBLE_QUERY, initialValue = false)
    private val password = MutableStateFlow("")

    //    private val refreshing = MutableStateFlow(false)
    private val screenState =
        savedSteHandle.getStateFlow(key = SCREEN_STATE_QUERY, initialValue = true)
    val progressState = mutableStateOf(false)
    private val errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _state = MutableStateFlow(AuthScreenState())

    val state: StateFlow<AuthScreenState>
        get() = _state

    init {

        viewModelScope.launch {
            combine(
                email,
                password,
                username,
                errorMessage,
                screenState,
            ) { email, password, username, errorMessage, screenState ->
                AuthScreenState(
                    email = email,
                    password = password,
                    username = username,
                    errorMessage = errorMessage,
//                    refreshing = array.get(4) as Boolean,
                    screenState = screenState,
//                    isSignIn = array.get(6) as Boolean,
                )

            }.catch {
                //Show error message
            }.collect {
                _state.value = it
            }
        }
    }

    fun showProgressState(state: Boolean) {
        progressState.value = state
    }

    fun saveUsernameToDatastore(username: String) {
        viewModelScope.launch {
            userSettingsDataRepository.setUserName(username)
        }
    }

    fun onEmailValueChange(email: String) {
        savedSteHandle[EMAIL_QUERY] = email
    }

    fun onPasswordValueChange(password: String) {
        this.password.value = password
    }

    fun onUsernameValueChange(username: String) {
        savedSteHandle[USERNAME_QUERY] = username
    }

    fun onScreenStateValueChange(screenState: Boolean) {
        savedSteHandle[SCREEN_STATE_QUERY] = !screenState
    }

    fun onPasswordVisible() {
        savedSteHandle[PASSWORD_VISIBLE_QUERY] = !passwordVisible.value
    }
}

data class AuthScreenState(
//    val refreshing: Boolean = false,
//    val isSignIn:Boolean = false,
    val screenState: Boolean = false,
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val errorMessage: String? = null
)

private const val EMAIL_QUERY = "emailQuery"
private const val USERNAME_QUERY = "usernameQuery"
private const val PASSWORD_VISIBLE_QUERY = "passwordVisibleQuery"
private const val SCREEN_STATE_QUERY = "screenStateQuery"