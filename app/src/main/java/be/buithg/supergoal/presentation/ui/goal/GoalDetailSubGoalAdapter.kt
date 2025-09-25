package be.buithg.supergoal.presentation.ui.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.databinding.ItemDetailSubgoalBinding

class GoalDetailSubGoalAdapter(
    private val onCheckedChanged: (Long, Boolean) -> Unit,
) : ListAdapter<GoalDetailSubGoalItem, GoalDetailSubGoalAdapter.SubGoalViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGoalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDetailSubgoalBinding.inflate(inflater, parent, false)
        return SubGoalViewHolder(binding, onCheckedChanged)
    }

    override fun onBindViewHolder(holder: SubGoalViewHolder, position: Int) {
        holder.bind(getItem(position), position == itemCount - 1)
    }

    class SubGoalViewHolder(
        private val binding: ItemDetailSubgoalBinding,
        private val onCheckedChanged: (Long, Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GoalDetailSubGoalItem, isLast: Boolean) = with(binding) {
            tvSubGoalTitle.text = item.title

            val isChecked = item.isCompleted
            checkContainer.isSelected = isChecked
            checkIcon.isVisible = isChecked
            divider.isVisible = !isLast

            val clickListener = {
                val newChecked = !checkContainer.isSelected
                checkContainer.isSelected = newChecked
                checkIcon.isVisible = newChecked
                onCheckedChanged(item.id, newChecked)
            }

            root.setOnClickListener { clickListener() }
            checkContainer.setOnClickListener { clickListener() }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<GoalDetailSubGoalItem>() {
        override fun areItemsTheSame(oldItem: GoalDetailSubGoalItem, newItem: GoalDetailSubGoalItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GoalDetailSubGoalItem, newItem: GoalDetailSubGoalItem): Boolean =
            oldItem == newItem
    }
}
