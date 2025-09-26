package be.buithg.supergoal.presentation.ui.challenges

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.ItemChallengeSubgoalBinding

class ChallengeSubGoalAdapter(
    private val onCheckedChange: (Long, Boolean) -> Unit,
) : ListAdapter<ChallengeSubGoalUi, ChallengeSubGoalAdapter.SubGoalViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGoalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChallengeSubgoalBinding.inflate(inflater, parent, false)
        return SubGoalViewHolder(binding, onCheckedChange)
    }

    override fun onBindViewHolder(holder: SubGoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SubGoalViewHolder(
        private val binding: ItemChallengeSubgoalBinding,
        private val onCheckedChange: (Long, Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChallengeSubGoalUi) = with(binding) {
            checkSubGoal.setOnCheckedChangeListener(null)
            checkSubGoal.text = item.title
            checkSubGoal.isChecked = item.isChecked
            checkSubGoal.buttonDrawable = AppCompatResources.getDrawable(
                root.context,
                R.drawable.selector_check_square,
            )
            checkSubGoal.buttonTintList = null
            checkSubGoal.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(item.id, isChecked)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ChallengeSubGoalUi>() {
        override fun areItemsTheSame(
            oldItem: ChallengeSubGoalUi,
            newItem: ChallengeSubGoalUi,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ChallengeSubGoalUi,
            newItem: ChallengeSubGoalUi,
        ): Boolean = oldItem == newItem
    }
}
