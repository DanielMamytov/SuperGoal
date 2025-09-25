package be.buithg.supergoal.presentation.ui.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentOnBoardingBinding
import be.buithg.supergoal.presentation.ui.onboarding.markOnboardingSeen

class OnBoardingFragment : Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnGetStarted.setOnClickListener {
            requireContext().markOnboardingSeen()
            findNavController().navigate(R.id.action_onBoardingFragment_to_nav_goals)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
