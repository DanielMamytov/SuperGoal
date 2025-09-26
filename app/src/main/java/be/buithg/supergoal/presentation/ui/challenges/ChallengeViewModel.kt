package be.buithg.supergoal.presentation.ui.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                ChallengeListItem(challenge = challenge, status = ChallengeStatus.NotStarted)
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
                _uiState.value = ChallengeListUiState(
                    challenges = allChallenges.map { challenge ->
                        val matchingGoal = goals.firstOrNull { it.title == challenge.title }
                        val status = when {
                            matchingGoal == null -> ChallengeStatus.NotStarted
                            matchingGoal.archivedAtMillis != null -> ChallengeStatus.Completed
                            else -> ChallengeStatus.Active
                        }
                        ChallengeListItem(
                            challenge = challenge,
                            status = status,
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
    val status: ChallengeStatus,
)
