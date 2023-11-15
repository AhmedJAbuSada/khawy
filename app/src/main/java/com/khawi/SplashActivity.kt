package com.khawi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.khawi.base.BaseActivity
import com.khawi.base.getPreferenceBoolean
import com.khawi.base.walkthrough_key
import com.khawi.databinding.ActivitySplashBinding
import com.khawi.model.db.user.UserRepository
import com.khawi.ui.login.LoginActivity
import com.khawi.ui.main.main.MainActivity
import com.khawi.ui.walkthrough.WalkthroughActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                val bundle = intent.extras
                val notification = bundle?.getString("notification")
                if (!notification.isNullOrEmpty()) {
                    startActivity(
                        Intent(this@SplashActivity, MainActivity::class.java)
                            .putExtra("notification", "notification")
                    )
                } else if (getPreferenceBoolean(walkthrough_key)) {
                    val user = repository.getUser()
                    val intent = Intent(
                        this@SplashActivity,
                        if (user != null && user.isVerify == true)
                            MainActivity::class.java
                        else
                            LoginActivity::class.java
                    )
                    startActivity(intent)
                } else {
                    val intent = Intent(this@SplashActivity, WalkthroughActivity::class.java)
                    startActivity(intent)
                }
                finishAffinity()
            }
        }, 2000)
    }
}