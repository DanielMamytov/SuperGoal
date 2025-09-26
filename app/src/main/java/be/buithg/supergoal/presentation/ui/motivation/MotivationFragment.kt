package be.buithg.supergoal.presentation.ui.motivation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.buithg.supergoal.databinding.FragmentMotivationBinding
import be.buithg.supergoal.presentation.ui.article.ArticleAdapter
import be.buithg.supergoal.presentation.ui.article.ArticleDataSource
import kotlin.random.Random

class MotivationFragment : Fragment() {

    private var _binding: FragmentMotivationBinding? = null
    private val binding get() = _binding!!

    private lateinit var articleAdapter: ArticleAdapter
    private var lastQuoteIndex: Int = -1

    private val motivationQuotes = listOf(
        MotivationQuote(
            text = "“Train for the ninety, prepare for the ninety-first.”",
            author = "— Marco Vale, Striker"
        ),
        MotivationQuote(
            text = "“First touch calms the ball; belief calms the storm.”",
            author = "— Elias Kade, Midfielder"
        ),
        MotivationQuote(
            text = "“We chase the ball, but we win the day with habits.”",
            author = "— Tomas Riera, Right Back"
        ),
        MotivationQuote(
            text = "“Sprint with your legs, recover with your mind.”",
            author = "— Luka Dervič, Winger"
        ),
        MotivationQuote(
            text = "“Pressure is just the crowd inside your head.”",
            author = "— Rafael Monte, Goalkeeper"
        ),
        MotivationQuote(
            text = "“Miss, learn, demand the next pass.”",
            author = "— Jonas Krell, Forward"
        ),
        MotivationQuote(
            text = "“Champions show up before the sun and stay after the lights.”",
            author = "— Victor Anoba, Centre-Back"
        ),
        MotivationQuote(
            text = "“Your lungs set limits; your purpose moves them.”",
            author = "— Niko Saar, Defensive Midfielder"
        ),
        MotivationQuote(
            text = "“Quality is repetition done with respect.”",
            author = "— Matteo Lora, Playmaker"
        ),
        MotivationQuote(
            text = "“Run the extra five yards; titles hide there.”",
            author = "— Pierre Dumez, Left Back"
        ),
        MotivationQuote(
            text = "“Make the pass you wish someone had made to you.”",
            author = "— Javier Roa, No. 10"
        ),
        MotivationQuote(
            text = "“Composure is speed that refuses to panic.”",
            author = "— Alexi Byrne, Striker"
        ),
        MotivationQuote(
            text = "“If your touch is honest, the game forgives a lot.”",
            author = "— Bruno Caetano, Wing-Back"
        ),
        MotivationQuote(
            text = "“Sweat is the ink; the season writes in it.”",
            author = "— Ilias Mour, Centre-Mid"
        ),
        MotivationQuote(
            text = "“Drills build trust so instincts can be brave.”",
            author = "— Daniel Hoene, Keeper"
        ),
        MotivationQuote(
            text = "“You can’t fake work when the ball rolls.”",
            author = "— Sergi Valdés, Holding Mid"
        ),
        MotivationQuote(
            text = "“We don’t chase luck; we create angles for it.”",
            author = "— Karim Fadil, Forward"
        ),
        MotivationQuote(
            text = "“Silence the noise with one clean first touch.”",
            author = "— Pavel Rusan, Centre-Back"
        ),
        MotivationQuote(
            text = "“The badge is heavy—carry it with small daily wins.”",
            author = "— Andrej Kovik, Captain"
        ),
        MotivationQuote(
            text = "“Aim for the far post; live for the next run.”",
            author = "— Diego Navas, Striker"
        ),
        MotivationQuote(
            text = "“Be humble in victory and curious in defeat.”",
            author = "— Mateo Zoric, Midfielder"
        ),
        MotivationQuote(
            text = "“Fitness fades; discipline survives extra time.”",
            author = "— Leon Varga, Full-Back"
        ),
        MotivationQuote(
            text = "“Talk less to the fear, more to the ball.”",
            author = "— Hugo Maret, Winger"
        ),
        MotivationQuote(
            text = "“Decision beats distance.”",
            author = "— Kaito Morita, Playmaker"
        ),
        MotivationQuote(
            text = "“Your off-the-ball runs tell the truth about you.”",
            author = "— Omar Benali, Forward"
        ),
        MotivationQuote(
            text = "“Big games reward small details done early.”",
            author = "— Stefan Groh, Centre-Back"
        ),
        MotivationQuote(
            text = "“Recovery is training in quieter clothes.”",
            author = "— Yassin Ferou, Keeper"
        ),
        MotivationQuote(
            text = "“We share the ball and multiply courage.”",
            author = "— Emil Petrescu, Midfielder"
        ),
        MotivationQuote(
            text = "“Plan the press, then trust the chaos.”",
            author = "— Rohan Dev, Wing-Back"
        ),
        MotivationQuote(
            text = "“Dream big, but keep your studs grounded.”",
            author = "— Luca Parisi, Striker"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMotivationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        articleAdapter.submitList(ArticleDataSource.getArticles())
        binding.btnRefreshQuote.setOnClickListener { displayRandomQuote() }
        displayRandomQuote()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.btnRefreshQuote.setOnClickListener(null)
        binding.rvMotivations.adapter = null
        _binding = null
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            val action = MotivationFragmentDirections.actionMotivationFragmentToNavArticle(article.id)
            findNavController().navigate(action)
        }
        binding.rvMotivations.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }
    }

    private fun displayRandomQuote() {
        if (motivationQuotes.isEmpty()) return
        var newIndex: Int
        do {
            newIndex = Random.nextInt(motivationQuotes.size)
        } while (motivationQuotes.size > 1 && newIndex == lastQuoteIndex)
        lastQuoteIndex = newIndex
        val quote = motivationQuotes[newIndex]
        binding.tvQuote.text = quote.text
        binding.tvQuoteAuthor.text = quote.author
    }
}

private data class MotivationQuote(
    val text: String,
    val author: String
)
