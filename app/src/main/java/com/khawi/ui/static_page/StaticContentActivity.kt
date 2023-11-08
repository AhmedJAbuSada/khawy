package com.khawi.ui.static_page

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.text.HtmlCompat
import com.kaopiz.kprogresshud.KProgressHUD
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.base.hideDialog
import com.khawi.base.initLoading
import com.khawi.base.showDialog
import com.khawi.databinding.ActivityStaticContentBinding


class StaticContentActivity : BaseActivity() {
    private var binding: ActivityStaticContentBinding? = null
    private var loading: KProgressHUD? = null
    private val viewModel: StaticPagesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaticContentBinding.inflate(layoutInflater)
        val view = binding?.root
        setContentView(view)

        binding?.back?.setOnClickListener {
            finish()
        }

        when {
            intent.hasExtra(about) -> {
                binding?.titlePage?.text = getString(R.string.about_us)
            }

            intent.hasExtra(terms) -> {
                binding?.titlePage?.text = getString(R.string.terms_conditions)
            }

            intent.hasExtra(privacy) -> {
                binding?.titlePage?.text = getString(R.string.privacy_policy)
            }
        }

        loading = initLoading()
        viewModel.progressLiveData.observe(this) {
            if (it) loading?.showDialog()
            else loading?.hideDialog()
        }
        viewModel.successLiveData.observe(this) {
            if (it?.status == true) {
                it.data?.let { list ->
                    for (value in list) {
                        if (value.type == "terms" && intent.hasExtra(terms)) {
                            binding?.content?.text = HtmlCompat.fromHtml(
                                value.content ?: "",
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            break
                        }
                        if (value.type == "about" && intent.hasExtra(about)) {
                            binding?.content?.text = HtmlCompat.fromHtml(
                                value.content ?: "",
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            break
                        }
                    }

                }
            }
        }


    }


    companion object {
        const val about = "about"
        const val terms = "terms"
        const val privacy = "privacy"
    }
}