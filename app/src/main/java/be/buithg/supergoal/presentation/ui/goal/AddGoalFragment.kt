package be.buithg.supergoal.presentation.ui.goal

import CategoryAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import be.buithg.supergoal.databinding.FragmentAddGoalBinding
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.LinearLayoutManager
import be.buithg.supergoal.presentation.ui.Category
import be.buithg.supergoal.presentation.ui.CategoryProvider

class AddGoalFragment : Fragment() {

    private lateinit var binding: FragmentAddGoalBinding

    // начальное значение категории — пусть будет Mind (id = 1)
    private var chosenCategory: Category = CategoryProvider.categories[1]

    private var categoryExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInlineCategory()
    }

    private fun setupInlineCategory() = with(binding) {
        val options = CategoryProvider.categories

        rvCategory.layoutManager = LinearLayoutManager(requireContext())
        rvCategory.adapter = CategoryAdapter(
            items = options.map { it.title },
            selected = options.indexOf(chosenCategory)
        ) { pos ->
            chosenCategory = options[pos]
            tvCategoryTitle.text = "Category: ${chosenCategory.title}"
            tvCategoryTitle.setTextColor(Color.WHITE)
        }
    }


    /** плавное раскрытие/сворачивание панели без скачков высоты */
    private fun togglePanel(panel: View, expand: Boolean) {
        if (expand) {
            panel.measure(
                View.MeasureSpec.makeMeasureSpec((panel.parent as View).width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val targetH = panel.measuredHeight
            panel.layoutParams.height = 0
            panel.visibility = View.VISIBLE
            ValueAnimator.ofInt(0, targetH).apply {
                duration = 160
                addUpdateListener {
                    panel.layoutParams.height = it.animatedValue as Int
                    panel.requestLayout()
                }
            }.start()
        } else {
            val startH = panel.height
            ValueAnimator.ofInt(startH, 0).apply {
                duration = 160
                addUpdateListener {
                    panel.layoutParams.height = it.animatedValue as Int
                    panel.requestLayout()
                }
                doOnEnd { panel.visibility = View.GONE }
            }.start()
        }
    }
}
