package com.khawi.ui.main.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.khawi.NavGraphDirections
import com.khawi.R
import com.khawi.base.errorMessage
import com.khawi.base.showAlertMessage
import com.khawi.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController
        viewModel.getUser()
        binding.bottomView.itemIconTintList = null

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.notifications) {
                viewModel.notificationCount()
            }
            binding.bottomView.visibility =
                if (destination.id == R.id.home
                    || destination.id == R.id.orders
                    || destination.id == R.id.notifications
                    || destination.id == R.id.settings
                )
                    View.VISIBLE
                else
                    View.GONE
            binding.requestForm.visibility =
                if (destination.id == R.id.home
                    || destination.id == R.id.orders
                    || destination.id == R.id.notifications
                    || destination.id == R.id.settings
                )
                    View.VISIBLE
                else
                    View.GONE
        }

        navController?.let {
            binding.bottomView.setupWithNavController(it)
        }

        val bottomNavigationViewBackground = binding.bottomView.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, 16f)
                .setTopLeftCorner(CornerFamily.ROUNDED, 16f)
                .build()

        binding.requestsFormGroup.visibility = View.GONE
        binding.requestForm.setOnClickListener {
            binding.requestsFormGroup.visibility = View.VISIBLE
        }
        binding.chooseForm.setOnClickListener {
            binding.requestsFormGroup.visibility = View.GONE
        }
        binding.deliverFormContainer.setOnClickListener {
            navController?.navigate(NavGraphDirections.actionRequestFormFragment(true))
            binding.requestsFormGroup.visibility = View.GONE
        }
        binding.joinFormContainer.setOnClickListener {
            val hasCar = viewModel.userMutableLiveData.value?.hasCar ?: false
            if (hasCar) {
                if (viewModel.userMutableLiveData.value?.isApprove == true) {
                    navController?.navigate(NavGraphDirections.actionRequestFormFragment(false))
                    binding.requestsFormGroup.visibility = View.GONE
                } else {
                    getString(R.string.request_sent_success).showAlertMessage(context = this,
                        title = getString(R.string.alert),
                        confirmText = getString(R.string.Ok),
                        type = SweetAlertDialog.WARNING_TYPE,
                        onCancelClick = {},
                        onConfirmClick = {

                        }
                    )
                }
            } else {
                getString(R.string.you_need_a_car).errorMessage(this)
            }
        }

        if (intent.hasExtra("notification"))
            binding.bottomView.selectedItemId = R.id.notifications

        viewModel.successLiveData.observe(this) {
            it?.data?.let { count ->
                if (count > 0) {
                    binding.bottomView.getOrCreateBadge(R.id.notifications).number = count
                } else {
                    binding.bottomView.removeBadge(R.id.notifications)
                }
            }
        }

        viewModel.notificationCount()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (resultCode) {
//            Activity.RESULT_OK -> {
//                val uri: Uri? = data?.data
//                if (uri != null)
//                    when (requestCode) {
//                        UpdateProfileFragment.PROFILE_IMAGE_REQ_CODE -> {
//                            viewModel.imageMutableLiveData.postValue(
//                                File(
//                                    FileUriUtils.getRealPath(
//                                        this,
//                                        uri
//                                    ) ?: ""
//                                )
//                            )
//                        }
//                    }
//            }
//
//            ImagePicker.RESULT_ERROR -> {
//                Log.e("image error", ImagePicker.getError(data))
//            }
//
//            else -> {
//                Log.e("cancelled", "Task Cancelled")
//            }
//        }
//    }
}