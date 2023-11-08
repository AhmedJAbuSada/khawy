package com.khawi.ui.contact_us

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
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.base.validateEmail
import com.khawi.databinding.FragmentContactUsBinding
import dagger.hilt.android.AndroidEntryPoint
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ContactUsFragment : Fragment() {

    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!
    private var loading: KProgressHUD? = null
    private val viewModel: ContactUSViewModel by viewModels()
    private var phoneNumberUtil: PhoneNumberUtil? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        phoneNumberUtil = PhoneNumberUtil.createInstance(requireContext())
        binding.ccp.registerCarrierNumberEditText(binding.phoneNumberET)

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.sendBtn.setOnClickListener {
            if (validation())
                viewModel.viewModelScope.launch {
                    viewModel.contactUs(
                        details = binding.messageET.text.toString(),
                        fullName = binding.fullNameET.text.toString(),
                        email = binding.emailET.text.toString(),
                        phoneNumber = binding.ccp.fullNumber
                    )
                }
        }

        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.fullNameET.setText(it.fullName)
                binding.emailET.setText(it.email)
                if ((it.phoneNumber ?: "").isNotEmpty()) {
                    val countryCode = getCountryIsoCode(it.phoneNumber ?: "")
                    if (countryCode?.isNotEmpty() == true)
                        binding.ccp.setCountryForPhoneCode(countryCode.toInt())
                    val phoneNumber = (it.phoneNumber ?: "").replace(countryCode ?: "", "")
                    binding.phoneNumberET.setText(phoneNumber)
                }
            }
        }

        loading = requireContext().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                it.message?.showAlertMessage(context = requireContext(),
                    title = getString(R.string.success),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.SUCCESS_TYPE,
                    onCancelClick = {},
                    onConfirmClick = {
                        findNavController().popBackStack()
                    })
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validation(): Boolean {
        if (binding.fullNameET.text.toString().isEmpty()) {
            getString(R.string.error_full_name_empty).showAlertMessage(context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                })
            return false
        }
        if (binding.emailET.text.toString().isEmpty()) {
            getString(R.string.error_email_empty).showAlertMessage(context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                })
            return false
        }
        if (!binding.emailET.text.toString().validateEmail()) {
            getString(R.string.error_email_invalid).showAlertMessage(context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                })
            return false
        }
        if (binding.phoneNumberET.text.toString().isEmpty()) {
            getString(R.string.error_phone_empty).showAlertMessage(context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                })
            return false
        }
        if (binding.messageET.text.toString().isEmpty()) {
            getString(R.string.error_message_empty).showAlertMessage(context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                })
            return false
        }
        return true
    }


    private fun getCountryIsoCode(number: String): String? {

        val validatedNumber = if (number.startsWith("+")) number else "+$number"

        val phoneNumber = try {
            phoneNumberUtil?.parse(validatedNumber, null)
        } catch (e: NumberParseException) {
            Timber.e("error during parsing a number")
            null
        } ?: return null

        val country = phoneNumberUtil?.getRegionCodeForCountryCode(phoneNumber.countryCode)
        return "${phoneNumberUtil?.getCountryCodeForRegion(country)}"
    }
}