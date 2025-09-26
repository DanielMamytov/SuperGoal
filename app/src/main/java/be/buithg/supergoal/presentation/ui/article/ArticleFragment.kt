package be.buithg.supergoal.presentation.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentArticleBinding

class ArticleFragment : Fragment() {

    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val article = ArticleDataSource.getArticles().firstOrNull { it.id == args.articleId }

        binding.apply {
            if (article != null) {
                tvArticleTitle.text = article.title
                tvArticleContent.text = article.content
                ivArticleCover.setImageResource(article.coverResId)
                ivArticleCover.visibility = View.VISIBLE
                ivArticleCover.contentDescription = article.title
            } else {
                tvArticleTitle.text = getString(R.string.article_not_found_title)
                tvArticleContent.text = getString(R.string.article_not_found_message)
                ivArticleCover.setImageDrawable(null)
                ivArticleCover.visibility = View.GONE
            }

            tvBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
