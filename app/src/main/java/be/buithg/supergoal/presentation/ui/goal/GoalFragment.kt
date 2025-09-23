package be.buithg.supergoal.presentation.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentArticleBinding
import be.buithg.supergoal.databinding.FragmentGoalBinding

class GoalFragment : Fragment() {

    private lateinit var binding: FragmentGoalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalBinding.inflate(inflater, container, false)
        return binding.root
    }


}