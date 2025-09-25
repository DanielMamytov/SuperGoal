package be.buithg.supergoal.presentation.ui.challenges

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.ChallengeItemBinding

class ChallengeAdapter(
    private val onChallengeClick: (Challenge) -> Unit,
) : ListAdapter<Challenge, ChallengeAdapter.ChallengeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChallengeItemBinding.inflate(inflater, parent, false)
        return ChallengeViewHolder(binding, onChallengeClick)
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        holder.bind(getItem(position))
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

    private object DiffCallback : DiffUtil.ItemCallback<Challenge>() {
        override fun areItemsTheSame(oldItem: Challenge, newItem: Challenge): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Challenge, newItem: Challenge): Boolean =
            oldItem == newItem
    }
}
