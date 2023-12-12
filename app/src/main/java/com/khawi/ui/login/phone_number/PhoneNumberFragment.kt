package com.khawi.ui.login.phone_number

import android.content.Intent
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
import com.birjuvachhani.locus.Locus
import com.google.android.gms.maps.model.LatLng
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.errorMessage
import com.khawi.base.getAddress
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.safeNavigate
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.databinding.FragmentPhoneNumberBinding
import com.khawi.ui.login.LoginViewModel
import com.khawi.ui.main.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PhoneNumberFragment : Fragment() {

    private var _binding: FragmentPhoneNumberBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val viewModel: PhoneNumberViewModel by viewModels()
    private var loading: KProgressHUD? = null
    private var latlng: LatLng? = null

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
        Locus.getCurrentLocation(requireContext()) { result ->
            result.location?.let {
                latlng = LatLng(it.latitude, it.longitude)
            }
            result.error?.let { }
        }
        binding.ccp.registerCarrierNumberEditText(binding.phoneNumberET)

        loading = requireActivity().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                it.data?.let { item ->
                    viewModel.addUser(item)
                    if (item.isApprove == false) {
                        it.message?.errorMessage(requireContext())
                    } else if (item.isVerify == false) {
                        findNavController().safeNavigate(
                            PhoneNumberFragmentDirections.actionPhoneNumberFragmentToVerificationFragment()
                        )
                    } else if (item.fullName.isNullOrEmpty()) {
                        findNavController().safeNavigate(
                            PhoneNumberFragmentDirections.actionPhoneNumberFragmentToUpdateProfileFragment()
                        )
                    } else {
                        startActivity(
                            Intent(requireContext(), MainActivity::class.java)
                        )
                        requireActivity().finishAffinity()
                    }
                }
            } else {
                it?.message?.errorMessage(requireContext())
            }
        }

        binding.signInBtn.setOnClickListener {
            val phone = binding.phoneNumberET.text.toString()
            if (phone.isNotEmpty()) {
                loginViewModel.phoneLiveData.postValue(binding.ccp.fullNumberWithPlus)
                viewModel.viewModelScope.launch {
                    viewModel.loginByPhone(
                        binding.ccp.fullNumber,
                        (latlng?.latitude ?: 0.0).toString(),
                        (latlng?.longitude ?: 0.0).toString(),
                        latlng?.getAddress(requireContext()) ?: ""
                    )
                }
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