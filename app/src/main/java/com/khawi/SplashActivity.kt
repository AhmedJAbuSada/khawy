package com.khawi

import android.content.Intent
import android.os.Bundle
import com.khawi.base.BaseActivity
import com.khawi.base.getPreferenceBoolean
import com.khawi.base.walkthrough_key
import com.khawi.databinding.ActivitySplashBinding
import com.khawi.ui.login.LoginActivity
import com.khawi.ui.walkthrough.WalkthroughActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

//    @Inject
//    lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timer().schedule(object : TimerTask() {
            override fun run() {
//                val bundle = intent.extras
//                val id = bundle?.getString("data")
//                val type = bundle?.getString("type")
//                if (!id.isNullOrEmpty() && !type.isNullOrEmpty()) {
//                    if (type == "message") {
//                        startActivity(
//                            Intent(applicationContext, ChatActivity::class.java)
//                                .putExtra(
//                                    ChatActivity.chatIdKey,
//                                    id
//                                )
//                        )
//                    }
//                } else
                if (getPreferenceBoolean(walkthrough_key)) {
//                    val user = repository.getUser()
                    val intent = Intent(
                        this@SplashActivity,
//                        if (user != null && user.isVerify == true)
//                            MainActivity::class.java
//                        else
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