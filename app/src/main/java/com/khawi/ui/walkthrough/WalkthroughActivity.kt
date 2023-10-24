package com.khawi.ui.walkthrough

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.base.savePreference
import com.khawi.base.walkthrough_key
import com.khawi.databinding.ActivityWalkthroughBinding
import com.khawi.model.Welcome
import com.khawi.ui.login.LoginActivity
import com.khawi.ui.walkthrough.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WalkthroughActivity : BaseActivity() {
    private lateinit var binding: ActivityWalkthroughBinding
    private val viewModel: WelcomeViewModel by viewModels()
    private var adapter: ViewPager2Adapter? = null
    private var currentPosition = 0
    private var items = mutableListOf<Welcome>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkthroughBinding.inflate(layoutInflater)
        setContentView(binding.root)

        savePreference(walkthrough_key, true)


        binding.skipBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        binding.continueBtn.setOnClickListener {
            if (currentPosition < items.size) {
                binding.viewpager.setCurrentItem(++currentPosition, true)
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finishAffinity()
            }
        }

        viewModel.getWelcome()

        viewModel.progressLiveData.observe(this) {

        }

        viewModel.successLiveData.observe(this) {
            if (it.isNotEmpty()) {
                currentPosition = 0
                items = it
                adapter = ViewPager2Adapter(this)
                adapter?.images = items
                binding.viewpager.adapter = adapter
                preparePages()
            }
        }
    }

    private fun getPageInfo(position: Int) {
        if (items.isEmpty()) return
        currentPosition = position
        val item = items[currentPosition]
        binding.stepTitle.text = item.name ?: ""
        binding.stepContent.text = item.description ?: ""

        if (currentPosition >= (items.size - 1))
            binding.continueBtn.text = getString(R.string.create_new_account)
        else
            binding.continueBtn.text = getString(R.string.next)
    }

    private fun preparePages() {
        binding.indicator.setViewPager(binding.viewpager)
        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                getPageInfo(position)
            }
        })
    }
}