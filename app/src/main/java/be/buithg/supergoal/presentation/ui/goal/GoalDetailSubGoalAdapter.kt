package be.buithg.supergoal.presentation.ui.goal

import android.view.LayoutInflater
import android.view.ViewGroup
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
        holder.bind(getItem(position))
    }

    class SubGoalViewHolder(
        private val binding: ItemDetailSubgoalBinding,
        private val onCheckedChanged: (Long, Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GoalDetailSubGoalItem) = with(binding) {
            taskCheckbox.setOnCheckedChangeListener(null)
            taskCheckbox.isChecked = item.isCompleted
            taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChanged(item.id, isChecked)
            }
            tvSubGoalTitle.text = item.title
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<GoalDetailSubGoalItem>() {
        override fun areItemsTheSame(oldItem: GoalDetailSubGoalItem, newItem: GoalDetailSubGoalItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GoalDetailSubGoalItem, newItem: GoalDetailSubGoalItem): Boolean =
            oldItem == newItem
    }
}
