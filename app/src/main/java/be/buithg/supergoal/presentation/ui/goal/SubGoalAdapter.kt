package be.buithg.supergoal.presentation.ui.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.databinding.ItemSubgoalBinding

class SubGoalAdapter(
    private val onDeleteClicked: (Long) -> Unit,
) : ListAdapter<SubGoalItemUi, SubGoalAdapter.SubGoalViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGoalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSubgoalBinding.inflate(inflater, parent, false)
        return SubGoalViewHolder(binding, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: SubGoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SubGoalViewHolder(
        private val binding: ItemSubgoalBinding,
        private val onDeleteClicked: (Long) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SubGoalItemUi) = with(binding) {
//            taskCheckbox.isChecked = item.isCompleted
//            taskCheckbox.isEnabled = false
            taskText.text = item.title
//            btnDeleteSubGoal.setOnClickListener { onDeleteClicked(item.uiId) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<SubGoalItemUi>() {
        override fun areItemsTheSame(oldItem: SubGoalItemUi, newItem: SubGoalItemUi): Boolean =
            oldItem.uiId == newItem.uiId

        override fun areContentsTheSame(oldItem: SubGoalItemUi, newItem: SubGoalItemUi): Boolean =
            oldItem == newItem
    }
}
