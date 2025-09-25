package be.buithg.supergoal.presentation.ui.goal

import CategoryAdapter
import android.animation.ValueAnimator
import android.app.DatePickerDialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
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

@AndroidEntryPoint
class AddGoalFragment : Fragment() {

    private var _binding: FragmentAddGoalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddGoalViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var subGoalAdapter: SubGoalAdapter

    private var categoryExpanded = false
    private var displayedImageUri: String? = null

    private val hintTextColor: Int by lazy { Color.parseColor("#87FFFFFF") }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it.toString()) }
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
        btnAddPhoto.setOnClickListener { pickImageLauncher.launch("image/*") }
        buttonSubGoal.setOnClickListener { showAddSubGoalDialog() }
        buttonSaveGoal.setOnClickListener { viewModel.onSaveGoal() }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateDeadline(state.deadlineText)
                    updateCategory(state)
                    updateSubGoals(state.subGoals)
                    updateImage(state.imageUri)
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
        if (categoryTitle != null) {
            text = getString(R.string.category_value, categoryTitle)
            setTextColor(Color.WHITE)
        } else {
            text = getString(R.string.category_placeholder)
            setTextColor(hintTextColor)
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
            if (parsedUri != null) {
                displayedImageUri = imageUri
                photoPreview.setImageURI(null)
                photoPreview.setImageURI(parsedUri)
                photoPlaceholder.isVisible = false
            } else {
                displayedImageUri = null
                photoPreview.setImageDrawable(null)
                photoPlaceholder.isVisible = true
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

    private fun showAddSubGoalDialog() {
        val input = AppCompatEditText(requireContext()).apply {
            hint = getString(R.string.add_subgoal_hint)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
        val container = FrameLayout(requireContext()).apply {
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            val margin = resources.getDimensionPixelSize(R.dimen.dialog_padding)
            params.leftMargin = margin
            params.rightMargin = margin
            addView(input, params)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_subgoal_title)
            .setView(container)
            .setPositiveButton(R.string.action_add, null)
            .setNegativeButton(R.string.action_cancel, null)
            .create()

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.setOnClickListener {
                val text = input.text?.toString().orEmpty().trim()
                if (text.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.error_empty_subgoal, Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.onAddSubGoal(text)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

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
