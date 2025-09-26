package be.buithg.supergoal.presentation.ui.challenges

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.ChallengeItemBinding
import be.buithg.supergoal.databinding.ChallengeItemCompletedBinding

class ChallengeAdapter(
    private val onChallengeClick: (Challenge) -> Unit,
) : ListAdapter<ChallengeListItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).isCompleted) VIEW_TYPE_COMPLETED else VIEW_TYPE_DEFAULT

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_COMPLETED) {
            val binding = ChallengeItemCompletedBinding.inflate(inflater, parent, false)
            CompletedViewHolder(binding, onChallengeClick)
        } else {
            val binding = ChallengeItemBinding.inflate(inflater, parent, false)
            ChallengeViewHolder(binding, onChallengeClick)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ChallengeViewHolder -> holder.bind(item.challenge)
            is CompletedViewHolder -> holder.bind(item.challenge)
        }
    }

    class ChallengeViewHolder(
        private val binding: ChallengeItemBinding,
        private val onChallengeClick: (Challenge) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Challenge) = with(binding) {
            ivCover.setImageResource(item.imageRes)
            tvTitle.text = item.title
            tvCategory.text = root.context.getString(R.string.challenge_category_format, item.category)
            tvCompletionTime.text = root.resources.getQuantityString(
                R.plurals.challenge_completion_time,
                item.durationDays,
                item.durationDays,
            )

            root.setOnClickListener { onChallengeClick(item) }
            btnAddGoal.setOnClickListener { onChallengeClick(item) }
        }
    }

    class CompletedViewHolder(
        private val binding: ChallengeItemCompletedBinding,
        private val onChallengeClick: (Challenge) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Challenge) = with(binding) {
            ivCover.setImageResource(item.imageRes)
            tvTitle.text = item.title
            tvDeadline.text = root.context.getString(R.string.challenge_category_format, item.category)
            tvStatus.text = root.context.getString(R.string.challenge_completed_status)
            root.setOnClickListener { onChallengeClick(item) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ChallengeListItem>() {
        override fun areItemsTheSame(oldItem: ChallengeListItem, newItem: ChallengeListItem): Boolean =
            oldItem.challenge.id == newItem.challenge.id

        override fun areContentsTheSame(oldItem: ChallengeListItem, newItem: ChallengeListItem): Boolean =
            oldItem == newItem
    }

    private companion object {
        const val VIEW_TYPE_DEFAULT = 0
        const val VIEW_TYPE_COMPLETED = 1
    }
}
