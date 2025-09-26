package be.buithg.supergoal.presentation.ui.goal

import CategoryAdapter
import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentAddGoalBinding
import be.buithg.supergoal.presentation.ui.CategoryProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlinx.coroutines.launch
import androidx.activity.result.contract.ActivityResultContracts
import android.view.inputmethod.EditorInfo

@AndroidEntryPoint
class AddGoalFragment : Fragment() {

    private var _binding: FragmentAddGoalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddGoalViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subGoalAdapter: SubGoalAdapter

    private var categoryExpanded = false
    private var displayedImageUri: String? = null

    private var shouldShowSubGoalWarning = false
    private var shouldShowFieldsWarning = false

    private val hintTextColor: Int by lazy { Color.parseColor("#87FFFFFF") }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            val resolver = requireContext().contentResolver
            try {
                resolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            } catch (securityException: SecurityException) {
                // Best effort to persist URI access, ignore if it already exists or can't be granted.
            }
            viewModel.onImageSelected(uri.toString())
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCategorySection()
        setupSubGoalList()
        setupListeners()
        collectState()
        collectEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCategory.adapter = null
        binding.rvSubGoals.adapter = null
        displayedImageUri = null
        _binding = null
    }

    private fun setupCategorySection() = with(binding) {
        val options = CategoryProvider.categories
        val initialIndex = options.indexOfFirst { it.goalCategory == viewModel.uiState.value.selectedCategory }
        categoryAdapter = CategoryAdapter(
            items = options.map { it.title },
            selected = initialIndex,
        ) { position ->
            val selectedCategory = options[position]
            viewModel.onCategorySelected(selectedCategory.goalCategory, selectedCategory.title)
            tvCategoryTitle.setTextColor(Color.WHITE)
            togglePanel(panelCategory, expand = false)
            categoryExpanded = false
        }
        rvCategory.layoutManager = LinearLayoutManager(requireContext())
        rvCategory.adapter = categoryAdapter

        tvCategoryTitle.setOnClickListener {
            categoryExpanded = !categoryExpanded
            togglePanel(panelCategory, categoryExpanded)
        }
    }

    private fun setupSubGoalList() {
        subGoalAdapter = SubGoalAdapter { id -> viewModel.onRemoveSubGoal(id) }
        binding.rvSubGoals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subGoalAdapter
        }
    }

    private fun setupListeners() = with(binding) {
        etGoalName.addTextChangedListener { text ->
            viewModel.onGoalNameChanged(text?.toString().orEmpty())
        }
        etCalendar.setOnClickListener { showDatePicker() }
        btnAddPhoto.setOnClickListener { pickImageLauncher.launch(arrayOf("image/*")) }
        photoPreview.setOnClickListener { pickImageLauncher.launch(arrayOf("image/*"))}
        buttonSubGoal.setOnClickListener { handleAddSubGoalInput() }
        etSubGoalInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handleAddSubGoalInput()
                true
            } else {
                false
            }
        }
        buttonSaveGoal.setOnClickListener { viewModel.onSaveGoal() }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateGoalName(state.goalName)
                    updateDeadline(state.deadlineText)
                    updateCategory(state)
                    updateSubGoals(state.subGoals)
                    updateImage(state.imageUri)
                    updateWarnings(state)
                }
            }
        }
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.events.collect { event ->
                    when (event) {
                        is AddGoalEvent.ShowMessage -> {
                            Toast.makeText(requireContext(), getString(event.messageRes), Toast.LENGTH_SHORT).show()
                            when (event.messageRes) {
                                R.string.toast_add_subgoal_first -> {
                                    shouldShowSubGoalWarning = true
                                }

                                R.string.toast_fill_all_fields -> {
                                    shouldShowFieldsWarning = true
                                }
                            }
                            updateWarnings(viewModel.uiState.value)
                        }

                        AddGoalEvent.GoalSaved -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun updateDeadline(deadlineText: String) = with(binding.etCalendar) {
        if (deadlineText.isNotBlank()) {
            text = deadlineText
            setTextColor(Color.WHITE)
        } else {
            text = getString(R.string.deadline_placeholder)
            setTextColor(hintTextColor)
        }
    }

    private fun updateCategory(state: AddGoalUiState) = with(binding.tvCategoryTitle) {
        val options = CategoryProvider.categories
        val selectedIndex = state.selectedCategory?.let { category ->
            options.indexOfFirst { it.goalCategory == category }
        } ?: -1
        if (selectedIndex != -1 && selectedIndex != categoryAdapter.selected) {
            categoryAdapter.selected = selectedIndex
            categoryAdapter.notifyDataSetChanged()
        } else if (selectedIndex == -1 && categoryAdapter.selected != -1) {
            categoryAdapter.selected = -1
            categoryAdapter.notifyDataSetChanged()
        }

        val categoryTitle = state.selectedCategoryTitle
            ?: state.selectedCategory?.let { category ->
                options.firstOrNull { it.goalCategory == category }?.title
            }
        if (categoryTitle != null) {
            text = getString(R.string.category_value, categoryTitle)
            setTextColor(Color.WHITE)
        } else {
            text = getString(R.string.category_placeholder)
            setTextColor(hintTextColor)
        }
    }

    private fun updateGoalName(goalName: String) = with(binding.etGoalName) {
        if (text?.toString() != goalName) {
            setText(goalName)
            setSelection(goalName.length)
        }
    }

    private fun updateSubGoals(subGoals: List<SubGoalItemUi>) = with(binding) {
        subGoalAdapter.submitList(subGoals)
        tvEmptySubGoals.isVisible = subGoals.isEmpty()
        rvSubGoals.isVisible = subGoals.isNotEmpty()
    }

    private fun updateImage(imageUri: String?) = with(binding) {
        if (imageUri.isNullOrBlank()) {
            if (displayedImageUri != null) {
                displayedImageUri = null
                photoPreview.setImageDrawable(null)
            }
            photoPlaceholder.isVisible = true
        } else if (displayedImageUri != imageUri) {
            val parsedUri = runCatching { Uri.parse(imageUri) }.getOrNull()
            val isResourceUri = parsedUri?.scheme == ContentResolver.SCHEME_ANDROID_RESOURCE
            val resourceId = if (isResourceUri) parsedUri?.lastPathSegment?.toIntOrNull() else null
            when {
                resourceId != null -> {
                    displayedImageUri = imageUri
                    photoPreview.setImageDrawable(null)
                    photoPreview.setImageResource(resourceId)
                    photoPlaceholder.isVisible = false
                }
                parsedUri != null -> {
                    displayedImageUri = imageUri
                    photoPreview.setImageURI(null)
                    photoPreview.setImageURI(parsedUri)
                    photoPlaceholder.isVisible = false
                }
                else -> {
                    displayedImageUri = null
                    photoPreview.setImageDrawable(null)
                    photoPlaceholder.isVisible = true
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        viewModel.uiState.value.deadlineMillis?.let { calendar.timeInMillis = it }
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                viewModel.onDeadlineSelected(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        )
        val minDateCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        dialog.datePicker.minDate = minDateCalendar.timeInMillis
        dialog.show()
    }

    private fun handleAddSubGoalInput() {
        val text = binding.etSubGoalInput.text?.toString().orEmpty().trim()
        if (text.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_empty_subgoal, Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.onAddSubGoal(text)
        binding.etSubGoalInput.text?.clear()
    }

    private fun updateWarnings(state: AddGoalUiState) = with(binding) {
        if (state.subGoals.isNotEmpty()) {
            shouldShowSubGoalWarning = false
        }
        if (state.hasAllRequiredFields()) {
            shouldShowFieldsWarning = false
        }
        tvSubGoalWarning.isVisible = shouldShowSubGoalWarning && state.subGoals.isEmpty()
        tvFieldsWarning.isVisible = shouldShowFieldsWarning && !state.hasAllRequiredFields()
    }

    private fun AddGoalUiState.hasAllRequiredFields(): Boolean =
        goalName.isNotBlank() && deadlineMillis != null && selectedCategory != null

    private fun togglePanel(panel: View, expand: Boolean) {
        if (expand) {
            panel.measure(
                View.MeasureSpec.makeMeasureSpec((panel.parent as View).width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            )
            val targetHeight = panel.measuredHeight
            panel.layoutParams.height = 0
            panel.visibility = View.VISIBLE
            ValueAnimator.ofInt(0, targetHeight).apply {
                duration = 160
                addUpdateListener { animator ->
                    panel.layoutParams.height = animator.animatedValue as Int
                    panel.requestLayout()
                }
            }.start()
        } else {
            val startHeight = panel.height
            ValueAnimator.ofInt(startHeight, 0).apply {
                duration = 160
                addUpdateListener { animator ->
                    panel.layoutParams.height = animator.animatedValue as Int
                    panel.requestLayout()
                }
                doOnEnd { panel.visibility = View.GONE }
            }.start()
        }
    }
}
