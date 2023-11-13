package com.khawi.ui.update_profile

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
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.errorMessage
import com.khawi.base.getAddress
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.loadImage
import com.khawi.base.showAlertMessage
import com.khawi.base.showDialog
import com.khawi.base.validateEmail
import com.khawi.databinding.FragmentUpdateProfileBinding
import com.khawi.ui.login.LoginActivity
import com.khawi.ui.login.LoginViewModel
import com.khawi.ui.main.main.MainActivity
import com.khawi.ui.main.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val viewModel: UpdateProfileViewModel by viewModels()
    private var loading: KProgressHUD? = null
    private var imageFile: File? = null
    private var latlng: LatLng? = null

    companion object {
        const val PROFILE_IMAGE_REQ_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Locus.getCurrentLocation(requireContext()) { result ->
            result.location?.let {
                latlng = LatLng(it.latitude, it.longitude)
            }
            result.error?.let { }
        }

        loading = requireActivity().initLoading()

        viewModel.userMutableLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.fullNameET.setText(it.fullName)
                binding.emailET.setText(it.email)
                binding.haveCarCheckBox.isChecked = it.hasCar ?: false
                val hasCar = binding.haveCarCheckBox.isChecked
                binding.carInformationContainer.visibility = View.GONE
                if (hasCar) {
                    binding.carInformationContainer.visibility = View.VISIBLE
                    binding.carTypeET.setText(it.carType)
                    binding.carModelET.setText(it.carModel)
                    binding.carColorET.setText(it.carColor)
                    binding.carPlateET.setText(it.carNumber)
                }
            }
        }

        binding.carInformationContainer.visibility = View.GONE
        binding.haveCarCheckBoxContainer.setOnClickListener {
            binding.haveCarCheckBox.isChecked = !binding.haveCarCheckBox.isChecked
            if (binding.haveCarCheckBox.isChecked) {
                binding.carInformationContainer.visibility = View.VISIBLE
            } else binding.carInformationContainer.visibility = View.GONE
        }

        binding.profileImageIV.setOnClickListener {
            ImagePicker.with(requireActivity()).cropSquare()
                .setImageProviderInterceptor { // Intercept ImageProvider
//                    Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
                }.maxResultSize(200, 200).start(PROFILE_IMAGE_REQ_CODE)
        }
        if (requireActivity() is LoginActivity)
            loginViewModel.imageMutableLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.profileImageIV.loadImage(requireContext(), it.path)
                    imageFile = it
                }
            }
        if (requireActivity() is MainActivity)
            mainViewModel.imageMutableLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.profileImageIV.loadImage(requireContext(), it.path)
                    imageFile = it
                }
            }

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.saveBtn.setOnClickListener {
            if (validation()) {
                val username = binding.fullNameET.text.toString()
                val email = binding.emailET.text.toString()
                val hasCar = binding.haveCarCheckBox.isChecked
                val carType = if (hasCar) binding.carTypeET.text.toString()
                else null
                val carModel = if (hasCar) binding.carModelET.text.toString()
                else null
                val carColor = if (hasCar) binding.carColorET.text.toString()
                else null
                val carPlate = if (hasCar) binding.carPlateET.text.toString()
                else null
                viewModel.viewModelScope.launch {
                    viewModel.updateUser(
                        email = email,
                        image = imageFile,
                        name = username,
                        hasCar = hasCar,
                        carType = carType,
                        carModel = carModel,
                        carColor = carColor,
                        carNumber = carPlate,
                        lat = (latlng?.latitude ?: 0.0).toString(),
                        lng = (latlng?.longitude ?: 0.0).toString(),
                        address = latlng?.getAddress(requireContext()) ?: ""
                    )
                }
            }
        }

        loading = requireActivity().initLoading()
        viewModel.progressLiveData.observe(viewLifecycleOwner) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(viewLifecycleOwner) {
            if (it?.status == true) {
                it.data?.let { item ->
                    viewModel.addUser(item)
                    it.message?.showAlertMessage(context = requireContext(),
                        title = getString(R.string.success),
                        confirmText = getString(R.string.Ok),
                        type = SweetAlertDialog.SUCCESS_TYPE,
                        onCancelClick = {},
                        onConfirmClick = {
                            if (requireActivity() is MainActivity) {
                                findNavController().popBackStack()
                            } else {
                                startActivity(
                                    Intent(
                                        requireContext(), MainActivity::class.java
                                    )
                                )
                                (requireActivity() as LoginActivity).finishAffinity()
                            }
                        })
                }
            } else {
                it?.message?.errorMessage(requireContext())
            }
        }
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
        if (binding.haveCarCheckBox.isChecked) {
            if (binding.carTypeET.text.toString().isEmpty()) {
                getString(R.string.error_car_type_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.carModelET.text.toString().isEmpty()) {
                getString(R.string.error_car_model_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.carColorET.text.toString().isEmpty()) {
                getString(R.string.error_car_color_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.carPlateET.text.toString().isEmpty()) {
                getString(R.string.error_car_plate_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
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