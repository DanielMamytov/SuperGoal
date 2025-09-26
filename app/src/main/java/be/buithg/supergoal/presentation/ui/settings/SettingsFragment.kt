package be.buithg.supergoal.presentation.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.buithg.supergoal.R
import be.buithg.supergoal.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnShareApp.setOnClickListener { shareApp() }
            btnRateUs.setOnClickListener { rateApp() }
            btnPrivacyPolicy.setOnClickListener {
                openStaticPage(R.string.settings_privacy_policy_title)
            }
            btnTermsAndConditions.setOnClickListener {
                openStaticPage(R.string.settings_terms_title)
            }
        }
    }

    private fun shareApp() {
        val context = requireContext()
        val packageName = context.packageName
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(
                    R.string.settings_share_message,
                    packageName,
                ),
            )
        }
        startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.settings_share_chooser_title),
            ),
        )
    }

    private fun rateApp() {
        val context = requireContext()
        val packageName = context.packageName
        val marketUri = Uri.parse("market://details?id=$packageName")
        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
        try {
            startActivity(marketIntent)
        } catch (e: ActivityNotFoundException) {
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            startActivity(Intent(Intent.ACTION_VIEW, webUri))
        }
    }

    private fun openStaticPage(@StringRes titleRes: Int) {
        val action = SettingsFragmentDirections.actionNavSettingsToStaticPageFragment(
            getString(titleRes),
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
