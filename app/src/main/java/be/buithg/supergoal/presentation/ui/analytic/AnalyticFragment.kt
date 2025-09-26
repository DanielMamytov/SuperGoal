package be.buithg.supergoal.presentation.ui.analytic

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentAnalyticBinding
import be.buithg.supergoal.databinding.ItemCategoryProgressBinding
import be.buithg.supergoal.domain.model.GoalCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@AndroidEntryPoint
class AnalyticFragment : Fragment() {

    private var _binding: FragmentAnalyticBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyticViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAnalyticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectUiState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::renderState)
            }
        }
    }

    private fun renderState(state: AnalyticUiState) = with(binding) {
        val hasOverallProgress = state.hasOverallProgress
        overallContainer.isVisible = hasOverallProgress
        tvOverallHeader.isVisible = hasOverallProgress

        if (state.hasOverallProgress) {
            val completedPercent = state.overallProgress.coerceIn(0, 100)
            cgProgress.progress = completedPercent
            tvCenterPercent.text = getString(R.string.analytics_percent_value, completedPercent)
        } else {
            cgProgress.progress = 0
            tvCenterPercent.text = getString(R.string.analytics_percent_value, 0)
        }

        val hasCategoryData = state.hasCategoryShares
        categoryContainer.isVisible = hasCategoryData
        tvCategoriesHeader.isVisible = hasCategoryData
        tvCategoriesEmpty.isVisible = !hasCategoryData

        if (hasCategoryData) {
            renderCategoryDistribution(state)
        } else {
            categoryContainer.removeAllViews()
        }
    }

    private fun renderCategoryDistribution(state: AnalyticUiState) {
        val inflater = LayoutInflater.from(requireContext())
        binding.categoryContainer.removeAllViews()
        val cornerRadius = resources.getDimension(R.dimen.category_progress_corner_radius)

        state.categoryShares.forEach { share ->
            val itemBinding = ItemCategoryProgressBinding.inflate(inflater, binding.categoryContainer, false)
            val percentValue = share.percentage.roundToInt().coerceIn(0, 100)
            val colorRes = share.category.toColorRes()
            val color = ContextCompat.getColor(requireContext(), colorRes)

            itemBinding.tvTitle.text = share.category.toDisplayName()
            itemBinding.tvLeftPercent.text = getString(R.string.analytics_percent_value, percentValue)

            // Dynamic color and progress update for the categories
            val rightCornerRadius = if (percentValue >= 100) cornerRadius else 0f
            val fillDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(color)
                cornerRadii = floatArrayOf(
                    cornerRadius,
                    cornerRadius,
                    rightCornerRadius,
                    rightCornerRadius,
                    rightCornerRadius,
                    rightCornerRadius,
                    cornerRadius,
                    cornerRadius,
                )
            }
            itemBinding.vLeftFill.background = fillDrawable
            itemBinding.guidelineSplit.setGuidelinePercent(percentValue / 100f)
            itemBinding.tvRightPercent.text = getString(R.string.analytics_percent_value, 100 - percentValue)
            binding.categoryContainer.addView(itemBinding.root)
        }
    }

    private fun GoalCategory.toColorRes(): Int = when (this) {
        GoalCategory.MIND -> R.color.analytics_mind
        GoalCategory.BODY -> R.color.analytics_body
        GoalCategory.CAREER -> R.color.analytics_career
        GoalCategory.MONEY -> R.color.analytics_money
        GoalCategory.SOCIAL -> R.color.analytics_social
        GoalCategory.OTHER -> R.color.analytics_other
    }

    private fun GoalCategory.toDisplayName(): String {
        val lowercase = name.lowercase(Locale.getDefault())
        return lowercase.replaceFirstChar { character ->
            if (character.isLowerCase()) character.titlecase(Locale.getDefault()) else character.toString()
        }
    }
}
