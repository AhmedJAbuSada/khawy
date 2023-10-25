package com.khawi.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khawi.databinding.FragmentSettingsBinding
import com.khawi.ui.login.LoginActivity
import com.khawi.ui.static_page.StaticContentActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateProfile.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToUpdateProfileFragment())
        }

        binding.walletContainer.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToWalletFragment())
        }
        binding.aboutUsContainer.setOnClickListener {
            startActivity(
                Intent(requireContext(), StaticContentActivity::class.java)
                    .putExtra(StaticContentActivity.about, StaticContentActivity.about)
            )
        }
        binding.contactUsContainer.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToContactUsFragment())
        }
        binding.shareAppContainer.setOnClickListener {}
        binding.termsConditionsContainer.setOnClickListener {
            startActivity(
                Intent(requireContext(), StaticContentActivity::class.java)
                    .putExtra(StaticContentActivity.terms, StaticContentActivity.terms)
            )
        }
        binding.logoutContainer.setOnClickListener {
            startActivity(
                Intent(requireContext(), LoginActivity::class.java)
            )
            requireActivity().finishAffinity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}