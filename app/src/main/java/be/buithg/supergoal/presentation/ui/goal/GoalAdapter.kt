package be.buithg.supergoal.presentation.ui.goal

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.GoalItemBinding

class GoalAdapter(
    private val onGoalClick: (GoalListItem) -> Unit,
) : ListAdapter<GoalListItem, GoalAdapter.GoalViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GoalItemBinding.inflate(inflater, parent, false)
        return GoalViewHolder(binding, onGoalClick)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GoalViewHolder(
        private val binding: GoalItemBinding,
        private val onGoalClick: (GoalListItem) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GoalListItem) = with(binding) {
            tvTitle.text = item.title
            tvCategory.text = item.category
            tvPercent.text = item.progressText
            pbProgress.progress = item.progressPercentage
            tvDeadline.text = item.deadline

            if (!item.imageUri.isNullOrBlank()) {
                val uri = runCatching { Uri.parse(item.imageUri) }.getOrNull()
                if (uri != null) {
                    ivCover.setImageURI(null)
                    ivCover.setImageURI(uri)
                } else {
                    ivCover.setImageResource(R.drawable.ic_launcher_background)
                }
            } else {
                ivCover.setImageResource(R.drawable.ic_launcher_background)
            }

            root.setOnClickListener { onGoalClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<GoalListItem>() {
        override fun areItemsTheSame(oldItem: GoalListItem, newItem: GoalListItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GoalListItem, newItem: GoalListItem): Boolean =
            oldItem == newItem
    }
}
