package com.khawi

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
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
        goNext()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDynamicLinks()
    }

    private fun handleDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {
                    val deepLink = pendingDynamicLinkData.link
                    val referralId = deepLink?.getQueryParameter("referal_id")
                    goNext(referralId)
                } else
                    goNext()

            }
            .addOnFailureListener(this) { e ->
                goNext()
                e.printStackTrace()
            }
    }

    private fun goNext(referralId: String? = null) {
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
                    val intent: Intent
                    if (user != null && user.isVerify == true) {
                        intent = Intent(this@SplashActivity, MainActivity::class.java)
                    } else {
                        intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        if (referralId != null)
                            intent.putExtra(LoginActivity.referralKey, referralId)
                    }
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