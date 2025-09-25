package be.buithg.supergoal.presentation.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentChallengeDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChallengeDetailFragment : Fragment() {

    private var _binding: FragmentChallengeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChallengeDetailViewModel by viewModels()

    private lateinit var subGoalAdapter: ChallengeSubGoalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChallengeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        collectState()
        collectEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.listSubGoals.adapter = null
        _binding = null
    }

    private fun setupRecyclerView() {
        subGoalAdapter = ChallengeSubGoalAdapter { id, isChecked ->
            viewModel.onSubGoalChecked(id, isChecked)
        }
        binding.listSubGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subGoalAdapter
        }
    }

    private fun setupListeners() = with(binding) {
        buttonBack.setOnClickListener { popIfPossible() }
        buttonStartChallenge.setOnClickListener { viewModel.onStartChallenge() }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> updateContent(state) }
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is ChallengeDetailEvent.ShowMessage -> {
                            Toast.makeText(
                                requireContext(),
                                getString(event.messageRes),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                        ChallengeDetailEvent.CloseScreen -> popIfPossible()
                    }
                }
            }
        }
    }

    private fun updateContent(state: ChallengeDetailUiState) = with(binding) {
        textTitle.text = state.title
        textCategoryValue.text = state.category
        imageIllustration.setImageResource(
            if (state.illustrationRes != 0) state.illustrationRes else R.drawable.challenge_screen_ic,
        )

        subGoalAdapter.submitList(state.subGoals)
        textEmptySubGoals.isVisible = state.isSubGoalListEmpty
        listSubGoals.isVisible = !state.isSubGoalListEmpty

        if (state.deadlineText.isNotBlank()) {
            val relativeText = resources.getQuantityString(
                R.plurals.challenge_detail_deadline_suffix,
                state.durationDays,
                state.durationDays,
            )
            textDeadlineValue.text = getString(
                R.string.challenge_detail_deadline_value,
                state.deadlineText,
                relativeText,
            )
        } else {
            textDeadlineValue.text = getString(R.string.challenge_detail_deadline_placeholder)
        }

        cardContent.isVisible = state.hasContent
        buttonStartChallenge.isEnabled = state.hasContent
        buttonStartChallenge.alpha = if (state.hasContent) 1f else 0.5f
    }

    private fun popIfPossible() {
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.challengeDetailFragment) {
            navController.popBackStack()
        }
    }
}
