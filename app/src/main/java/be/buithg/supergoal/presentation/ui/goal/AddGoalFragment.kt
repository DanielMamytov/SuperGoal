package be.buithg.supergoal.presentation.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentAddGoalBinding

class AddGoalFragment : Fragment() {

    private lateinit var binding: FragmentAddGoalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGoalBinding.inflate(inflater, container, false)
        return binding.root
    }


}