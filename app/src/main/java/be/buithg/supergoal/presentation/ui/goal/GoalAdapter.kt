package be.buithg.supergoal.presentation.ui.goal

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.GoalItemBinding
import be.buithg.supergoal.databinding.GoalItemCompletedBinding

class GoalAdapter(
    private val onGoalClick: (GoalListItem) -> Unit,
) : ListAdapter<GoalListItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).progressPercentage >= COMPLETED_PROGRESS) {
            VIEW_TYPE_COMPLETED
        } else {
            VIEW_TYPE_DEFAULT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_COMPLETED -> {
                val binding = GoalItemCompletedBinding.inflate(inflater, parent, false)
                GoalCompletedViewHolder(binding, onGoalClick)
            }
            else -> {
                val binding = GoalItemBinding.inflate(inflater, parent, false)
                GoalViewHolder(binding, onGoalClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is GoalViewHolder -> holder.bind(item)
            is GoalCompletedViewHolder -> holder.bind(item)
        }
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

    class GoalCompletedViewHolder(
        private val binding: GoalItemCompletedBinding,
        private val onGoalClick: (GoalListItem) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: GoalListItem) = with(binding) {
            tvTitle.text = item.title
            tvCategory.text = item.category
            tvTextCompleted.text = root.context.getString(R.string.challenge_completed_status)
            tvDeadline2.text = item.deadline

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

    private companion object {
        const val VIEW_TYPE_DEFAULT = 0
        const val VIEW_TYPE_COMPLETED = 1
        const val COMPLETED_PROGRESS = 100
    }

    private object DiffCallback : DiffUtil.ItemCallback<GoalListItem>() {
        override fun areItemsTheSame(oldItem: GoalListItem, newItem: GoalListItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: GoalListItem, newItem: GoalListItem): Boolean =
            oldItem == newItem
    }
}
