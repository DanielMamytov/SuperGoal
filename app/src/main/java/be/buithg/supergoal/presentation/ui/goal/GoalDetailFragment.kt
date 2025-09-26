package be.buithg.supergoal.presentation.ui.goal

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import be.buithg.supergoal.databinding.FragmentGoalDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GoalDetailFragment : Fragment() {

    private var _binding: FragmentGoalDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GoalDetailViewModel by viewModels()

    private lateinit var subGoalAdapter: GoalDetailSubGoalAdapter

    private var displayedImageUri: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGoalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.pbProgress.max = 1000
        setupListeners()
        collectState()
        collectEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.subGoals.adapter = null
        displayedImageUri = null
        _binding = null
    }

    private fun setupRecyclerView() {
        subGoalAdapter = GoalDetailSubGoalAdapter { id, isChecked ->
            viewModel.onSubGoalToggled(id, isChecked)
        }
        binding.subGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subGoalAdapter
        }
    }

    private fun setupListeners() = with(binding) {
        editButton.setOnClickListener {
            viewModel.uiState.value.goalId?.let { goalId ->
                val args = bundleOf("goalId" to goalId)
                findNavController().navigate(R.id.addGoalFragment, args)
            }
        }
        deleteButton.setOnClickListener { showDeleteConfirmationDialog() }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateContent(state)
                }
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is GoalDetailEvent.GoalDeleted -> {
                            Toast.makeText(requireContext(), event.messageRes, Toast.LENGTH_SHORT).show()
                            popIfPossible()
                        }

                        GoalDetailEvent.CloseScreen -> {
                            popIfPossible()
                        }
                    }
                }
            }
        }
    }

    private fun popIfPossible() {
        if (findNavController().currentDestination?.id == R.id.goalDetailFragment) {
            findNavController().popBackStack()
        }
    }

    private fun updateContent(state: GoalDetailUiState) = with(binding) {
        titleText.text = state.title
        categoryText.text = if (state.category.isNotBlank()) {
            getString(R.string.category_value, state.category)
        } else {
            getString(R.string.category_placeholder)
        }
        tvPercent.text = state.progressText
        pbProgress.progress = state.progressValue
        tvDeadline.text = if (state.deadline.isNotBlank()) {
            getString(R.string.goal_deadline_value, state.deadline)
        } else {
            getString(R.string.goal_deadline_placeholder)
        }
        updateImage(state)
        subGoalAdapter.submitList(state.subGoals)
        tvEmptySubGoals.isVisible = state.isSubGoalListEmpty
        subGoals.isVisible = !state.isSubGoalListEmpty
    }

    private fun updateImage(state: GoalDetailUiState) = with(binding) {
        if (state.showPlaceholderImage) {
            if (displayedImageUri != null) {
                displayedImageUri = null
                photoPreview.setImageDrawable(null)
            }
            photoPreview.isVisible = true
        } else if (displayedImageUri != state.imageUri) {
            val uri = runCatching { Uri.parse(state.imageUri) }.getOrNull()
            val isResourceUri = uri?.scheme == ContentResolver.SCHEME_ANDROID_RESOURCE
            val resourceId = if (isResourceUri) uri?.lastPathSegment?.toIntOrNull() else null
            when {
                resourceId != null -> {
                    displayedImageUri = state.imageUri
                    photoPreview.setImageDrawable(null)
                    photoPreview.setImageResource(resourceId)
                    photoPreview.isVisible = false
                }
                uri != null -> {
                    displayedImageUri = state.imageUri
                    photoPreview.setImageURI(null)
                    photoPreview.setImageURI(uri)
                    photoPreview.isVisible = false
                }
                else -> {
                    displayedImageUri = null
                    photoPreview.setImageDrawable(null)
                    photoPreview.isVisible = true
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.delete_goal_confirmation)
            .setPositiveButton(R.string.action_confirm) { _, _ -> viewModel.onDeleteGoal() }
            .setNegativeButton(R.string.action_cancel, null)
            .show()
    }
}
