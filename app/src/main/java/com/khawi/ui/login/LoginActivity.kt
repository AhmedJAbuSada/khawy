package com.khawi.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var navController: NavController? = null

    private val viewModel: LoginViewModel by viewModels()

    companion object {
        const val referralKey = "referral"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(referralKey))
            viewModel.referralIdLiveData.postValue(intent.getStringExtra(referralKey))

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_login) as NavHostFragment
        navController = navHostFragment.navController

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (resultCode) {
//            Activity.RESULT_OK -> {
//                val uri: Uri = data?.data!!
//                when (requestCode) {
//                    UpdateProfileFragment.PROFILE_IMAGE_REQ_CODE -> {
//                        viewModel.imageMutableLiveData.postValue(
//                            File(
//                                FileUriUtils.getRealPath(
//                                    this,
//                                    uri
//                                ) ?: ""
//                            )
//                        )
//                    }
//                }
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