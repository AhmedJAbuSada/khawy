package com.khawi.ui.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.loadImage
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentSettingsBinding
import com.khawi.ui.login.LoginActivity
import com.khawi.ui.static_page.StaticContentActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by viewModels()
    private var loading: KProgressHUD? = null

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
        loading = requireActivity().initLoading()

        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.userImage.loadImage(requireContext(), it.image)
                binding.username.text = it.fullName
            }
        }
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
        binding.shareAppContainer.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "https://play.google.com/store/apps/details?id=${requireActivity().packageName}"
                )
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, null))
        }
        binding.termsConditionsContainer.setOnClickListener {
            startActivity(
                Intent(requireContext(), StaticContentActivity::class.java)
                    .putExtra(StaticContentActivity.terms, StaticContentActivity.terms)
            )
        }
        binding.logoutContainer.setOnClickListener {
            getString(R.string.are_you_want_logout).showAlertMessage(context = requireContext(),
                title = getString(R.string.alert),
                confirmText = getString(R.string.Ok),
                cancelText = getString(R.string.close_),
                type = SweetAlertDialog.WARNING_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {
                    viewModel.viewModelScope.launch {
                        viewModel.logout()
                    }
                })
        }

        loading = requireActivity().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                viewModel.deleteAll()
                startActivity(
                    Intent(requireContext(), LoginActivity::class.java)
                )
                requireActivity().finishAffinity()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}