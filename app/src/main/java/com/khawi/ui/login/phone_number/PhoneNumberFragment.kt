package com.khawi.ui.login.phone_number

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.initLoading
import com.khawi.base.safeNavigate
import com.khawi.base.showAlertMessage
import com.khawi.databinding.FragmentPhoneNumberBinding
import com.khawi.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhoneNumberFragment : Fragment() {

    private var _binding: FragmentPhoneNumberBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val viewModel: PhoneNumberViewModel by viewModels()
    private var loading: KProgressHUD? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = requireActivity().initLoading()

        binding.ccp.registerCarrierNumberEditText(binding.phoneNumberET)

        binding.signInBtn.setOnClickListener {
            val phone = binding.phoneNumberET.text.toString()
            if (phone.isNotEmpty()) {
                findNavController().safeNavigate(
                    PhoneNumberFragmentDirections.actionPhoneNumberFragmentToVerificationFragment()
                )
//                loading?.showDialog()
//                viewModel.viewModelScope.launch {
//                    viewModel.loginByPhone(binding.ccp.fullNumber).collect {
//                        when (it) {
//                            is BaseState.NetworkError -> {
//                                loading?.hideDialog()
//                            }
//
//                            is BaseState.EmptyResult -> {
//                                loading?.hideDialog()
//                            }
//
//                            is BaseState.ItemsLoaded -> {
//                                loading?.hideDialog()
//                                if (it.items?.status == true) {
//                                    it.items.data?.let { item ->
//                                        loginViewModel.phoneLiveData.postValue(phone)
//                                        viewModel.addUser(item)
//                                        if (item.isVerify == false)
//                                            findNavController().safeNavigate(
//                                                PhoneNumberFragmentDirections.actionPhoneNumberFragmentToVerificationFragment()
//                                            )
//                                        else if (item.enName.isNullOrEmpty())
//                                            findNavController().safeNavigate(
//                                                PhoneNumberFragmentDirections.actionPhoneNumberFragmentToUpdateProfileFragment()
//                                            )
//                                        else {
//                                            startActivity(
//                                                Intent(requireContext(), MainActivity::class.java)
//                                            )
//                                            requireActivity().finishAffinity()
//                                        }
//
//                                    }
//                                } else {
//                                    it.items?.message?.errorMessage(requireContext())
//                                }
//                            }
//
//                            else -> {
//                                loading?.hideDialog()
//                            }
//                        }
//                    }
//                }
            } else {
                getString(R.string.error_phone_empty).showAlertMessage(
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