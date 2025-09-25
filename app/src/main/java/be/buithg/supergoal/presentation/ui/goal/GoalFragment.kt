package be.buithg.supergoal.presentation.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentGoalBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoalFragment : Fragment() {

    private var _binding: FragmentGoalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalViewModel by viewModels()

    private val goalAdapter = GoalAdapter { goal ->
        val action = GoalFragmentDirections.actionNavGoalsToGoalDetailFragment(goal.id)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClicks()
        observeUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() = with(binding.rvGoals) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = goalAdapter
        setHasFixedSize(false)
    }

    private fun setupClicks() = with(binding) {
        btnAddGoal.setOnClickListener {
            findNavController().navigate(R.id.action_nav_goals_to_addGoalFragment)
        }

        btnAll.setOnClickListener { viewModel.onFilterSelected(GoalFilter.ALL) }
        btnActive.setOnClickListener { viewModel.onFilterSelected(GoalFilter.ACTIVE) }
        btnArchived.setOnClickListener { viewModel.onFilterSelected(GoalFilter.ARCHIVED) }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    goalAdapter.submitList(state.goals)
                    binding.tvEmptyState.isVisible = state.isEmpty
                    updateFilterState(state.selectedFilter)
                }
            }
        }
    }

    private fun updateFilterState(selectedFilter: GoalFilter) = with(binding) {
        fun TextView.applyState(isSelected: Boolean) {
            setBackgroundResource(
                if (isSelected) R.drawable.bg_tab_selected else R.drawable.bg_tab_unselected
            )
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }

        btnAll.applyState(selectedFilter == GoalFilter.ALL)
        btnActive.applyState(selectedFilter == GoalFilter.ACTIVE)
        btnArchived.applyState(selectedFilter == GoalFilter.ARCHIVED)
    }
}
