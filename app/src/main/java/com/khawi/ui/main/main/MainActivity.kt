package com.khawi.ui.main.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUriUtils
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.khawi.NavGraphDirections
import com.khawi.R
import com.khawi.databinding.ActivityMainBinding
import com.khawi.ui.update_profile.UpdateProfileFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

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

        binding.bottomView.itemIconTintList = null

        navController?.addOnDestinationChangedListener { _, destination, _ ->
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
            navController?.navigate(NavGraphDirections.actionRequestFormFragment(false))
            binding.requestsFormGroup.visibility = View.GONE
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                val uri: Uri? = data?.data
                if (uri != null)
                    when (requestCode) {
                        UpdateProfileFragment.PROFILE_IMAGE_REQ_CODE -> {
                            viewModel.imageMutableLiveData.postValue(
                                File(
                                    FileUriUtils.getRealPath(
                                        this,
                                        uri
                                    ) ?: ""
                                )
                            )
                        }
                    }
            }

            ImagePicker.RESULT_ERROR -> {
                Log.e("image error", ImagePicker.getError(data))
            }

            else -> {
                Log.e("cancelled", "Task Cancelled")
            }
        }
    }
}