package com.khawi.ui.login.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.TimerCode
import com.khawi.base.errorMessage
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.safeNavigate
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentVerificationBinding
import com.khawi.network_base.model.BaseState
import com.khawi.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerificationFragment : Fragment() {

    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val viewModel: VerificationCodeViewModel by viewModels()
    private var loading: KProgressHUD? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.content.text =
            "${getString(R.string.code_verification_content)} ${loginViewModel.phoneLiveData.value}"

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.timer.setOnClickListener {
            if (binding.timer.text.toString() == getString(R.string.resend_code)) {
                viewModel.viewModelScope.launch {
                    viewModel.resendCode().collect {
                        when (it) {
                            is BaseState.NetworkError -> {
                            }

                            is BaseState.EmptyResult -> {
                            }

                            is BaseState.ItemsLoaded -> {
                                if (it.items?.status == true)
                                    binding.timer.TimerCode(
                                        getString(R.string.you_can_resend_code_after),
                                        getString(R.string.resend_code),
                                        120
                                    )
                            }

                            else -> {
                            }
                        }
                    }
                }
            }
        }
        binding.timer.TimerCode(
            getString(R.string.you_can_resend_code_after),
            getString(R.string.resend_code),
            120
        )
        loading = requireActivity().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                it.data?.let { item ->
                    viewModel.addUser(item)
                    findNavController().safeNavigate(
                        VerificationFragmentDirections.actionVerificationFragmentToUpdateProfileFragment()
                    )
                }
            } else {
                it?.message?.errorMessage(requireContext())
            }
        }
        binding.verifyBtn.setOnClickListener {
            val code = binding.codePinView.text.toString()
            if (code.isNotEmpty()) {
                viewModel.viewModelScope.launch {
                    viewModel.verifyPhone(code, loginViewModel.referralIdLiveData.value)
                }
            } else {
                getString(R.string.error_verification_empty).showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}