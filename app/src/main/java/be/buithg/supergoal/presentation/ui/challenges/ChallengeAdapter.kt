package be.buithg.supergoal.presentation.ui.challenges

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.ChallengeItemActivBinding
import be.buithg.supergoal.databinding.ChallengeItemBinding
import be.buithg.supergoal.databinding.ChallengeItemCompletedBinding

class ChallengeAdapter(
    private val onChallengeClick: (Challenge) -> Unit,
) : ListAdapter<ChallengeListItem, RecyclerView.ViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position).status) {
            ChallengeStatus.Completed -> VIEW_TYPE_COMPLETED
            ChallengeStatus.Active -> VIEW_TYPE_ACTIVE
            ChallengeStatus.NotStarted -> VIEW_TYPE_DEFAULT
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_COMPLETED -> {
                val binding = ChallengeItemCompletedBinding.inflate(inflater, parent, false)
                CompletedViewHolder(binding, onChallengeClick)
            }

            VIEW_TYPE_ACTIVE -> {
                val binding = ChallengeItemActivBinding.inflate(inflater, parent, false)
                ActiveViewHolder(binding, onChallengeClick)
            }

            else -> {
                val binding = ChallengeItemBinding.inflate(inflater, parent, false)
                ChallengeViewHolder(binding, onChallengeClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is ChallengeViewHolder -> holder.bind(item)
            is ActiveViewHolder -> holder.bind(item)
            is CompletedViewHolder -> holder.bind(item)
        }
    }

    class ChallengeViewHolder(
        private val binding: ChallengeItemBinding,
        private val onChallengeClick: (Challenge) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChallengeListItem) = with(binding) {
            val challenge = item.challenge
            ivCover.setImageResource(challenge.imageRes)
            tvTitle.text = challenge.title
            tvCategory.text = root.context.getString(R.string.challenge_category_format, challenge.category)
            tvCompletionTime.text = root.resources.getQuantityString(
                R.plurals.challenge_completion_time,
                challenge.durationDays,
                challenge.durationDays,
            )

            btnAddGoal.isVisible = true

            root.setOnClickListener { onChallengeClick(challenge) }
            btnAddGoal.setOnClickListener { onChallengeClick(challenge) }
        }
    }

    class ActiveViewHolder(
        private val binding: ChallengeItemActivBinding,
        private val onChallengeClick: (Challenge) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChallengeListItem) = with(binding) {
            val challenge = item.challenge
            ivCover.setImageResource(challenge.imageRes)
            tvTitle.text = challenge.title
            tvCategory.text = root.context.getString(R.string.challenge_category_format, challenge.category)
            tvCompletionTime.text = root.resources.getQuantityString(
                R.plurals.challenge_completion_time,
                challenge.durationDays,
                challenge.durationDays,
            )
            statusActive.text = root.context.getString(R.string.challenge_detail_active)

            root.setOnClickListener { onChallengeClick(challenge) }
        }
    }

    class CompletedViewHolder(
        private val binding: ChallengeItemCompletedBinding,
        private val onChallengeClick: (Challenge) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChallengeListItem) = with(binding) {
            val challenge = item.challenge
            ivCover.setImageResource(challenge.imageRes)
            tvTitle.text = challenge.title
            tvDeadline.text = root.context.getString(R.string.challenge_category_format, challenge.category)
            tvStatus.text = root.context.getString(R.string.challenge_completed_status)
            root.setOnClickListener { onChallengeClick(challenge) }
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
        const val VIEW_TYPE_ACTIVE = 2
    }
}
