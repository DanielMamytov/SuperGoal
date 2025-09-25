package be.buithg.supergoal.presentation.ui.analytic

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentAnalyticBinding
import be.buithg.supergoal.databinding.ItemAnalyticsLegendBinding
import be.buithg.supergoal.domain.model.GoalCategory
import be.buithg.supergoal.presentation.ui.custom.AnalyticsPieChartView
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

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
        overallContainer.isVisible = state.hasOverallProgress
        tvOverallEmpty.isVisible = !state.hasOverallProgress

        if (state.hasOverallProgress) {
            val completedPercent = state.overallProgress.coerceIn(0, 100)
            val remainingPercent = (100 - completedPercent).coerceAtLeast(0)

            updateOverallBar(completedPercent, remainingPercent)
            tvOverallPercent.text = getString(R.string.analytics_percent_value, completedPercent)
            tvOverallRemainingPercent.text = getString(R.string.analytics_percent_value, remainingPercent)
        }

        val hasCategoryData = state.hasCategoryShares
        pieChartView.isVisible = hasCategoryData
        legendContainer.isVisible = hasCategoryData
        tvPieEmpty.isVisible = !hasCategoryData

        if (hasCategoryData) {
            val slices = state.categoryShares.map { share ->
                val color = ContextCompat.getColor(requireContext(), share.category.toColorRes())
                AnalyticsPieChartView.Slice(
                    fraction = (share.percentage / 100.0).toFloat(),
                    color = color,
                )
            }
            pieChartView.setData(slices)
            renderLegend(state)
        } else {
            pieChartView.setData(emptyList())
            legendContainer.removeAllViews()
        }
    }

    private fun FragmentAnalyticBinding.updateOverallBar(
        completedPercent: Int,
        remainingPercent: Int,
    ) {
        val hasCompleted = completedPercent > 0
        val hasRemaining = remainingPercent > 0

        overallProgressCompletedSegment.isVisible = hasCompleted
        overallProgressRemainingSegment.isVisible = hasRemaining

        tvOverallPercent.isVisible = hasCompleted
        tvOverallRemainingPercent.isVisible = hasRemaining

        overallProgressCompletedSegment.updateLayoutParams<LinearLayout.LayoutParams> {
            weight = if (hasCompleted) completedPercent.toFloat() else 0f
        }
        overallProgressRemainingSegment.updateLayoutParams<LinearLayout.LayoutParams> {
            weight = if (hasRemaining) remainingPercent.toFloat() else 0f
        }

        val completedBackground = if (hasRemaining) {
            R.drawable.bg_overall_progress_completed
        } else {
            R.drawable.bg_overall_progress_completed_full
        }
        val remainingBackground = if (hasCompleted) {
            R.drawable.bg_overall_progress_remaining
        } else {
            R.drawable.bg_overall_progress_remaining_full
        }

        overallProgressCompletedSegment.setBackgroundResource(completedBackground)
        overallProgressRemainingSegment.setBackgroundResource(remainingBackground)
    }

    private fun renderLegend(state: AnalyticUiState) {
        val inflater = LayoutInflater.from(requireContext())
        binding.legendContainer.removeAllViews()
        state.categoryShares.forEach { share ->
            val itemBinding = ItemAnalyticsLegendBinding.inflate(inflater, binding.legendContainer, false)
            val colorRes = share.category.toColorRes()
            val color = ContextCompat.getColor(requireContext(), colorRes)
            ViewCompat.setBackgroundTintList(itemBinding.vLegendColor, ColorStateList.valueOf(color))
            itemBinding.tvLegendTitle.text = share.category.toDisplayName()
            itemBinding.tvLegendPercent.text = getString(
                R.string.analytics_percent_value,
                share.percentage.roundToInt(),
            )
            binding.legendContainer.addView(itemBinding.root)
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
