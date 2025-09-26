package be.buithg.supergoal.presentation.ui.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import be.buithg.supergoal.databinding.MotivationItemBinding

class ArticleAdapter(
    private val onItemClick: (Article) -> Unit,
) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MotivationItemBinding.inflate(inflater, parent, false)
        return ArticleViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ArticleViewHolder(
        private val binding: MotivationItemBinding,
        private val onItemClick: (Article) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.ivCover.setImageResource(article.coverResId)
            binding.tvTitle.text = article.title
            binding.tvDeadline.text = article.content
            binding.root.setOnClickListener { onItemClick(article) }
        }
    }

    private object ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem == newItem
    }
}
