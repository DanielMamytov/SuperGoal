package be.buithg.supergoal.presentation.ui.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentSplashScreenBinding
import be.buithg.supergoal.presentation.ui.onboarding.hasSeenOnboarding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MS = 1400L

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashScreenBinding? = null
    private val binding get() = _binding!!

    private var progressAnim: ValueAnimator? = null
    private var pulseX: ObjectAnimator? = null
    private var pulseY: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startLogoPulse()
        startProgressLoop()
        launchNavigationCountdown()
    }

    private fun startLogoPulse() {
        pulseX = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_X, 1f, 1.06f).apply {
            duration = 700
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
        pulseY = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_Y, 1f, 1.06f).apply {
            duration = 700
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun startProgressLoop() {
        binding.progressCapsule.progress = 0
        progressAnim = ValueAnimator.ofInt(0, 100).apply {
            duration = 600L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                binding.progressCapsule.progress = anim.animatedValue as Int
            }
            start()
        }
    }

    private fun launchNavigationCountdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(SPLASH_DELAY_MS)
            if (!isAdded) return@launch
            val nextDestination = if (requireContext().hasSeenOnboarding()) {
                R.id.action_splashFragment_to_nav_goals
            } else {
                R.id.action_splashFragment_to_onBoardingFragment
            }
            stopAnimations()
            findNavController().navigate(nextDestination)
        }
    }

    private fun stopAnimations() {
        progressAnim?.cancel()
        pulseX?.cancel()
        pulseY?.cancel()
        binding.ivLogo.scaleX = 1f
        binding.ivLogo.scaleY = 1f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAnimations()
        progressAnim = null
        pulseX = null
        pulseY = null
        _binding = null
    }
}
