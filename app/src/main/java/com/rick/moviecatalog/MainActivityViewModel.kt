package com.rick.moviecatalog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.settings.data_settings.data.repository.UserSettingsDataRepository
import com.rick.settings.data_settings.model.SettingsUserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserSettingsDataRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        MainActivityUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val showSettingsDialog: MutableLiveData<Boolean> = MutableLiveData(false)
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userData: SettingsUserData) : MainActivityUiState
}
