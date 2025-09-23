package be.buithg.supergoal.presentation.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentSettingsBinding
import be.buithg.supergoal.databinding.FragmentSplashScreenBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


}