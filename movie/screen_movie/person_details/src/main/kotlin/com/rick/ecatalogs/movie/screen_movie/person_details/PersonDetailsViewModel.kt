package com.rick.ecatalogs.movie.screen_movie.person_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rick.ecatalogs.movie.data_movie.domain.GetPersonUseCase
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    getPersonUseCase: GetPersonUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    /** Navigation argument (deep link `person_details_fragment/{person}`). */
    private val personId = savedStateHandle.getStateFlow(PERSON_ID, -1)

    /** Bumped by [retry] to load again after an error. */
    private val attempt = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PersonUiState> = combine(personId, attempt) { id, _ -> id }
        .flatMapLatest { id ->
            if (id == -1) {
                flowOf(PersonUiState.Loading)
            } else {
                getPersonUseCase(id)
                    .map<Person, PersonUiState>(PersonUiState::Success)
                    .onStart { emit(PersonUiState.Loading) }
                    .catch { emit(PersonUiState.Error) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PersonUiState.Loading,
        )

    fun retry() {
        attempt.value++
    }
}

sealed interface PersonUiState {
    data object Loading : PersonUiState
    data class Success(val person: Person) : PersonUiState
    data object Error : PersonUiState
}

private const val PERSON_ID = "person"
