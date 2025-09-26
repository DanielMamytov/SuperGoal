package be.buithg.supergoal.presentation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import be.buithg.supergoal.databinding.FragmentStaticPageBinding

class StaticPageFragment : Fragment() {

    private var _binding: FragmentStaticPageBinding? = null
    private val binding get() = _binding!!
    private val args: StaticPageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStaticPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = args.pageTitle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
