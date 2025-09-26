package be.buithg.supergoal.presentation.ui.motivation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentArticleBinding
import be.buithg.supergoal.databinding.FragmentMotivationBinding
import be.buithg.supergoal.presentation.ui.article.ArticleAdapter
import be.buithg.supergoal.presentation.ui.article.ArticleDataSource

class MotivationFragment : Fragment() {

    private var _binding: FragmentMotivationBinding? = null
    private val binding get() = _binding!!

    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMotivationBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        articleAdapter.submitList(ArticleDataSource.getArticles())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvMotivations.adapter = null
        _binding = null
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter()
        binding.rvMotivations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }
    }
}