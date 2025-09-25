package be.buithg.supergoal.presentation.ui.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentSettingsBinding
import be.buithg.supergoal.databinding.FragmentSplashScreenBinding



class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashScreenBinding
    private var progressAnim: ValueAnimator? = null
    private var pulseX: ObjectAnimator? = null
    private var pulseY: ObjectAnimator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Пульс логотипа (бесконечный, лёгкий)
        pulseX = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_X, 1f, 1.06f).apply {
            duration = 700
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
        pulseY = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_Y, 1f, 1.06f).apply {
            duration = 700
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }

        // 2) Прогресс с задержкой старта
        binding.progressCapsule.progress = 0
        progressAnim = ValueAnimator.ofInt(0, 100).apply {
            startDelay = 600L            // <- задержка
            duration = 1800L
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { anim ->
                binding.progressCapsule.progress = anim.animatedValue as Int
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    stopPulses()
                    // TODO: перейти дальше по навигации:
                    // findNavController().navigate(R.id.action_splash_to_home)
                }
            })
            start()
        }
    }

    private fun stopPulses() {
        pulseX?.cancel(); pulseY?.cancel()
        binding.ivLogo.scaleX = 1f
        binding.ivLogo.scaleY = 1f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressAnim?.cancel()
        stopPulses()
    }
}
