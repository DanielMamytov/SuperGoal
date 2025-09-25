package be.buithg.supergoal.presentation.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import be.buithg.supergoal.databinding.FragmentGoalDetailBinding

class GoalDetailFragment : Fragment() {

    private var _binding: FragmentGoalDetailBinding? = null
    private val binding get() = _binding!!

    private val args: GoalDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.tag = args.goalId
        // TODO: consume goalId when detail screen is implemented
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
