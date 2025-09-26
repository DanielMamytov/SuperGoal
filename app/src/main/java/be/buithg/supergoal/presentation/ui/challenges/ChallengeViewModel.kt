package be.buithg.supergoal.presentation.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.buithg.supergoal.domain.model.Goal
import be.buithg.supergoal.domain.usecase.GoalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val goalUseCases: GoalUseCases,
) : ViewModel() {

    private val allChallenges = ChallengeDataSource.getChallenges()

    private val _uiState = MutableStateFlow(
        ChallengeListUiState(
            challenges = allChallenges.map { challenge ->
                ChallengeListItem(challenge = challenge, isCompleted = false)
            },
        ),
    )
    val uiState: StateFlow<ChallengeListUiState> = _uiState.asStateFlow()

    init {
        observeGoals()
    }

    private fun observeGoals() {
        viewModelScope.launch {
            goalUseCases.observeGoals().collect { goals ->
                val completedTitles = goals
                    .filter { it.archivedAtMillis != null }
                    .map(Goal::title)
                    .toSet()
                _uiState.value = ChallengeListUiState(
                    challenges = allChallenges.map { challenge ->
                        ChallengeListItem(
                            challenge = challenge,
                            isCompleted = completedTitles.contains(challenge.title),
                        )
                    },
                )
            }
        }
    }
}

data class ChallengeListUiState(
    val challenges: List<ChallengeListItem> = emptyList(),
)

data class ChallengeListItem(
    val challenge: Challenge,
    val isCompleted: Boolean,
)
