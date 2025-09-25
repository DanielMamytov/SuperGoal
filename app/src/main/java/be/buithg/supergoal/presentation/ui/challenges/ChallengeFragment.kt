package be.buithg.supergoal.presentation.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentChallengeBinding

class ChallengeFragment : Fragment() {

    private var _binding: FragmentChallengeBinding? = null
    private val binding get() = _binding!!

    private lateinit var challengeAdapter: ChallengeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        challengeAdapter.submitList(ChallengeDataSource.getChallenges())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvChallenges.adapter = null
        _binding = null
    }

    private fun setupRecyclerView() {
        challengeAdapter = ChallengeAdapter(onChallengeClick = ::openChallengeDetails)
        binding.rvChallenges.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = challengeAdapter
        }
    }

    private fun openChallengeDetails(challenge: Challenge) {
        val arguments = bundleOf("challengeId" to challenge.id)
        findNavController().navigate(R.id.challengeDetailFragment, arguments)
    }
}
