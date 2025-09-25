package be.buithg.supergoal.presentation.ui.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.buithg.supergoal.domain.usecase.GoalUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class GoalViewModel @Inject constructor(
    goalUseCases: GoalUseCases
) : ViewModel() {

    private val selectedFilter = MutableStateFlow(GoalFilter.ALL)

    private val goalsFlow = goalUseCases.observeGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val uiState: StateFlow<GoalUiState> = combine(selectedFilter, goalsFlow) { filter, goals ->
        val filteredGoals = when (filter) {
            GoalFilter.ALL -> goals
            GoalFilter.ACTIVE -> goals.filterNot { it.isCompleted }
            GoalFilter.ARCHIVED -> goals.filter { it.isCompleted }
        }

        GoalUiState(
            goals = filteredGoals,
            selectedFilter = filter
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GoalUiState()
    )

    fun onFilterSelected(filter: GoalFilter) {
        if (selectedFilter.value != filter) {
            selectedFilter.value = filter
        }
    }
}
