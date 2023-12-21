package com.khawi.ui.update_profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
import com.khawi.ui.main.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class UpdateProfileFragment : Fragment() {

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!
//    private val loginViewModel: LoginViewModel by activityViewModels()
//    private val mainViewModel: MainViewModel by activityViewModels()

    private val viewModel: UpdateProfileViewModel by viewModels()
    private var loading: KProgressHUD? = null
    private var imageFile: File? = null
    private var latlng: LatLng? = null
    private var identityImageFile: File? = null
    private var licenseImageFile: File? = null
    private var carFrontImageFile: File? = null
    private var carBackImageFile: File? = null
    private var carRightImageFile: File? = null
    private var carLeftImageFile: File? = null

//    companion object {
//        const val PROFILE_IMAGE_REQ_CODE = 101
//    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val fileUri = data?.data!!
                    imageFile = fileUri.path?.let { File(it) }
                    binding.profileImageIV.loadImage(Uri.fromFile(imageFile))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val identityImageFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val fileUri = data?.data!!
                    identityImageFile = fileUri.path?.let { File(it) }
                    binding.identityImage.text = identityImageFile?.name ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val licenseImageFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val fileUri = data?.data!!
                    licenseImageFile = fileUri.path?.let { File(it) }
                    binding.licenseImage.text = licenseImageFile?.name ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val carFrontImageFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val fileUri = data?.data!!
                    carFrontImageFile = fileUri.path?.let { File(it) }
                    binding.carFrontImage.text = carFrontImageFile?.name ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val carBackImageFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val fileUri = data?.data!!
                    carBackImageFile = fileUri.path?.let { File(it) }
                    binding.carBackImage.text = carBackImageFile?.name ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val carRightImageFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val fileUri = data?.data!!
                    carRightImageFile = fileUri.path?.let { File(it) }
                    binding.carRightImage.text = carRightImageFile?.name ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private val carLeftImageFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                try {
                    val fileUri = data?.data!!
                    carLeftImageFile = fileUri.path?.let { File(it) }
                    binding.carLeftImage.text = carLeftImageFile?.name ?: ""
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
                binding.profileImageIV.loadImage(it.image ?: "")
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

                    binding.identityImage.text =
                        if ((it.identityImage ?: "").isNotEmpty())
                            getString(R.string.image_was_attached)
                        else
                            ""
                    binding.licenseImage.text =
                        if ((it.licenseImage ?: "").isNotEmpty())
                            getString(R.string.image_was_attached)
                        else
                            ""
                    binding.carFrontImage.text =
                        if ((it.carFrontImage ?: "").isNotEmpty())
                            getString(R.string.image_was_attached)
                        else
                            ""
                    binding.carBackImage.text =
                        if ((it.carBackImage ?: "").isNotEmpty())
                            getString(R.string.image_was_attached)
                        else
                            ""
                    binding.carRightImage.text =
                        if ((it.carRightImage ?: "").isNotEmpty())
                            getString(R.string.image_was_attached)
                        else
                            ""
                    binding.carLeftImage.text =
                        if ((it.carLeftImage ?: "").isNotEmpty())
                            getString(R.string.image_was_attached)
                        else
                            ""
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
            ImagePicker.with(requireActivity())
                .compress(512)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
//                .setImageProviderInterceptor { // Intercept ImageProvider
////                    Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
//                }.maxResultSize(200, 200).start(PROFILE_IMAGE_REQ_CODE)
        }

//        if (requireActivity() is LoginActivity)
//            loginViewModel.imageMutableLiveData.observe(viewLifecycleOwner) {
//                if (it != null) {
//                    binding.profileImageIV.loadImage(requireContext(), it.path)
//                    imageFile = it
//                }
//            }
//        if (requireActivity() is MainActivity)
//            mainViewModel.imageMutableLiveData.observe(viewLifecycleOwner) {
//                if (it != null) {
//                    binding.profileImageIV.loadImage(requireContext(), it.path)
//                    imageFile = it
//                }
//            }

        binding.identityImage.setOnClickListener {
            ImagePicker.with(requireActivity())
                .compress(512)
                .createIntent { intent ->
                    identityImageFileResult.launch(intent)
                }
        }
        binding.licenseImage.setOnClickListener {
            ImagePicker.with(requireActivity())
                .compress(512)
                .createIntent { intent ->
                    licenseImageFileResult.launch(intent)
                }
        }
        binding.carFrontImage.setOnClickListener {
            ImagePicker.with(requireActivity())
                .compress(512)
                .createIntent { intent ->
                    carFrontImageFileResult.launch(intent)
                }
        }
        binding.carBackImage.setOnClickListener {
            ImagePicker.with(requireActivity())
                .compress(512)
                .createIntent { intent ->
                    carBackImageFileResult.launch(intent)
                }
        }
        binding.carRightImage.setOnClickListener {
            ImagePicker.with(requireActivity())
                .compress(512)
                .createIntent { intent ->
                    carRightImageFileResult.launch(intent)
                }
        }
        binding.carLeftImage.setOnClickListener {
            ImagePicker.with(requireActivity())
                .compress(512)
                .createIntent { intent ->
                    carLeftImageFileResult.launch(intent)
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
                        address = latlng?.getAddress(requireContext()) ?: "",
                        identityImageFile = identityImageFile,
                        licenseImageFile = licenseImageFile,
                        carFrontImageFile = carFrontImageFile,
                        carBackImageFile = carBackImageFile,
                        carRightImageFile = carRightImageFile,
                        carLeftImageFile = carLeftImageFile,
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
                    it.message?.showAlertMessage(context = requireContext(),
                        title = getString(R.string.success),
                        confirmText = getString(R.string.Ok),
                        type = SweetAlertDialog.SUCCESS_TYPE,
                        onCancelClick = {},
                        onConfirmClick = {
                            if (requireActivity() is MainActivity) {
                                viewModel.addUser(item)
                                findNavController().popBackStack()
                            } else {
                                if (item.isApprove == true) {
                                    viewModel.addUser(item)
                                    startActivity(
                                        Intent(
                                            requireContext(), MainActivity::class.java
                                        )
                                    )
                                    (requireActivity() as LoginActivity).finishAffinity()
                                } else {
                                    findNavController().navigate(R.id.action_updateProfileFragment_to_phoneNumberFragment)
                                }
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
            if (binding.identityImage.text.toString().isEmpty()) {
                getString(R.string.error_identity_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.licenseImage.text.toString().isEmpty()) {
                getString(R.string.error_license_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.carFrontImage.text.toString().isEmpty()) {
                getString(R.string.error_car_front_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.carBackImage.text.toString().isEmpty()) {
                getString(R.string.error_car_back_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.carRightImage.text.toString().isEmpty()) {
                getString(R.string.error_car_right_empty).showAlertMessage(context = requireContext(),
                    title = getString(R.string.error),
                    confirmText = getString(R.string.Ok),
                    type = SweetAlertDialog.ERROR_TYPE,
                    onCancelClick = {

                    },
                    onConfirmClick = {

                    })
                return false
            }
            if (binding.carLeftImage.text.toString().isEmpty()) {
                getString(R.string.error_car_left_empty).showAlertMessage(context = requireContext(),
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