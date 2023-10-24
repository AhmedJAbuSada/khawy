package com.khawi.ui.login.username

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import cn.pedant.SweetAlert.SweetAlertDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.ui.main.MainActivity
import com.khawi.R
import com.khawi.base.initLoading
import com.khawi.base.loadImage
import com.khawi.base.showAlertMessage
import com.khawi.base.validateEmail
import com.khawi.databinding.FragmentUpdateProfileBinding
import com.khawi.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
//    private val viewModel: UpdateProfileViewModel by viewModels()
    private var loading: KProgressHUD? = null
    private var imageFile: File? = null

    companion object {
        const val PROFILE_IMAGE_REQ_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loading = requireActivity().initLoading()

        binding.carInformationContainer.visibility = View.GONE
        binding.haveCarCheckBoxContainer.setOnClickListener {
            binding.haveCarCheckBox.isChecked = !binding.haveCarCheckBox.isChecked
            if (binding.haveCarCheckBox.isChecked) {
                binding.carInformationContainer.visibility = View.VISIBLE
            }
        }

        binding.profileImageIV.setOnClickListener {
            ImagePicker.with(requireActivity())
                .cropSquare()
                .setImageProviderInterceptor { // Intercept ImageProvider
//                    Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
                }
                .maxResultSize(200, 200)
                .start(PROFILE_IMAGE_REQ_CODE)
        }
        loginViewModel.imageMutableLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.profileImageIV.loadImage(requireContext(), it.path)
                imageFile = it
            }
        }

        binding.saveBtn.setOnClickListener {
//            val username = binding.fullNameET.text.toString()
            if (validation()) {
                startActivity(
                    Intent(requireContext(), MainActivity::class.java)
                )
                requireActivity().finishAffinity()
//                loading?.showDialog()
//                viewModel.viewModelScope.launch {
//                    viewModel.updateUser(name = username).collect {
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
//                                        viewModel.addUser(item)
//                                        startActivity(
//                                            Intent(requireContext(), MainActivity::class.java)
//                                        )
//                                        requireActivity().finishAffinity()
//                                    }
//
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
            }
        }
    }

    private fun validation(): Boolean {
        if (binding.fullNameET.text.toString().isEmpty()) {
            getString(R.string.error_full_name_empty).showAlertMessage(
                context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                }
            )
            return false
        }
        if (binding.emailET.text.toString().isEmpty()) {
            getString(R.string.error_email_empty).showAlertMessage(
                context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                }
            )
            return false
        }
        if (!binding.emailET.text.toString().validateEmail()) {
            getString(R.string.error_email_invalid).showAlertMessage(
                context = requireContext(),
                title = getString(R.string.error),
                confirmText = getString(R.string.Ok),
                type = SweetAlertDialog.ERROR_TYPE,
                onCancelClick = {

                },
                onConfirmClick = {

                }
            )
            return false
        }
        if (binding.haveCarCheckBox.isChecked){
            if (binding.carTypeET.text.toString().isEmpty()) {
                getString(R.string.error_car_type_empty).showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
                return false
            }
            if (binding.carModelET.text.toString().isEmpty()) {
                getString(R.string.error_car_model_empty).showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
                return false
            }
            if (binding.carColorET.text.toString().isEmpty()) {
                getString(R.string.error_car_color_empty).showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
                return false
            }
            if (binding.carPlateET.text.toString().isEmpty()) {
                getString(R.string.error_car_plate_empty).showAlertMessage(
                    context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    }
                )
                return false
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}