package be.buithg.supergoal.presentation.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
    private lateinit var goalAdapter: GoalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClicks()
        collectUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvGoals.adapter = null
        _binding = null
    }

    private fun setupRecyclerView() {
        goalAdapter = GoalAdapter(onGoalClick = ::openGoalDetails)
        binding.rvGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalAdapter
        }
    }

    private fun setupClicks() = with(binding) {
        btnAddGoal.setOnClickListener {
            findNavController().navigate(R.id.addGoalFragment)
        }
        btnAll.setOnClickListener { viewModel.onFilterSelected(GoalFilter.All) }
        btnActive.setOnClickListener { viewModel.onFilterSelected(GoalFilter.Active) }
        btnArchived.setOnClickListener { viewModel.onFilterSelected(GoalFilter.Archived) }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    goalAdapter.submitList(state.goals)
                    binding.tvEmptyState.isVisible = state.isEmpty
                    binding.rvGoals.isVisible = state.goals.isNotEmpty()
                    updateFilterSelection(state.selectedFilter)
                }
            }
        }
    }

    private fun updateFilterSelection(selectedFilter: GoalFilter) = with(binding) {
        val selectedBackground = R.drawable.bg_tab_selected
        val unselectedBackground = R.drawable.bg_tab_unselected

        btnAll.setBackgroundResource(
            if (selectedFilter == GoalFilter.All) selectedBackground else unselectedBackground,
        )
        btnActive.setBackgroundResource(
            if (selectedFilter == GoalFilter.Active) selectedBackground else unselectedBackground,
        )
        btnArchived.setBackgroundResource(
            if (selectedFilter == GoalFilter.Archived) selectedBackground else unselectedBackground,
        )
    }

    private fun openGoalDetails(goal: GoalListItem) {
        val arguments = bundleOf("goalId" to goal.id)
        findNavController().navigate(R.id.goalDetailFragment, arguments)
    }
}
