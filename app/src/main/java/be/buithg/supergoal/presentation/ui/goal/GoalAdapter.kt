package be.buithg.supergoal.presentation.ui.goal

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.GoalItemBinding
import be.buithg.supergoal.domain.model.Goal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

class GoalAdapter(
    private val onGoalClicked: (Goal) -> Unit
) : ListAdapter<Goal, GoalAdapter.GoalViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GoalItemBinding.inflate(inflater, parent, false)
        return GoalViewHolder(binding, onGoalClicked)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GoalViewHolder(
        private val binding: GoalItemBinding,
        private val onGoalClicked: (Goal) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(goal: Goal) = with(binding) {
            tvTitle.text = goal.title
            tvCategory.text = goal.category.name.lowercase(Locale.getDefault()).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }

            val progressValue = goal.progress.roundToInt().coerceIn(0, 100)
            tvPercent.text = itemView.context.getString(R.string.goal_percent_format, progressValue)
            pbProgress.progress = progressValue

            val deadlineText = dateFormatter.format(Date(goal.deadlineMillis))
            tvDeadline.text = deadlineText

            if (goal.imageUri.isNullOrBlank()) {
                ivCover.setImageDrawable(
                    ContextCompat.getDrawable(itemView.context, R.drawable.image_placeholder)
                )
            } else {
                ivCover.setImageDrawable(null)
                ivCover.setImageURI(Uri.parse(goal.imageUri))
                if (ivCover.drawable == null) {
                    ivCover.setImageDrawable(
                        ContextCompat.getDrawable(itemView.context, R.drawable.image_placeholder)
                    )
                }
            }

            root.setOnClickListener { onGoalClicked(goal) }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Goal>() {
        override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean = oldItem == newItem
    }
}
