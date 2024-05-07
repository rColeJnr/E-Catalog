package com.rick.moviecatalog

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.settings.data_settings.data.repository.UserSettingsDataRepository
import com.rick.settings.data_settings.model.SettingsUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userDataRepository: UserSettingsDataRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        MainActivityUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val showSettingsDialog = MutableLiveData(false)

    val showChangeUsername = MutableStateFlow(false)
    val showDeleteButton = MutableStateFlow(false)

    val username: MutableState<String> = mutableStateOf("")

    fun shouldShowChangeUsername() {
        showChangeUsername.value = showChangeUsername.value.not()
    }

    fun shouldShowDeleteButton() {
        showDeleteButton.value = showDeleteButton.value.not()
    }

    fun onChangeUsername(username: String) {
        this.username.value = username
    }

    fun onSaveUsername() {
        viewModelScope.launch {
            userDataRepository.setUserName(username.value)
        }
        shouldShowChangeUsername()
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: SettingsUserData) : MainActivityUiState
}
