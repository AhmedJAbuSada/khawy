package com.khawi.ui.static_page

import android.graphics.Color
import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.khawi.R
import com.khawi.base.BaseActivity
import com.khawi.databinding.ActivityStaticContentBinding


class StaticContentActivity : BaseActivity() {
    private var binding: ActivityStaticContentBinding? = null

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
//        callRequest()
    }

//    private fun callRequest() {
//        DataClint.instants(false).callIntro(this, object : CallBackRequestListener<BaseResponse> {
//            override fun onResponse(response: BaseResponse?) {
//                try {
//                    if (response?.status?.success == true) {
//                        when {
//                            intent.hasExtra(about) -> {
//                                binding?.titlePage?.text = getString(R.string.about_us)
//                                binding?.content?.text = HtmlCompat.fromHtml(
//                                    response.settings?.about_us ?: "",
//                                    HtmlCompat.FROM_HTML_MODE_LEGACY
//                                )
//                            }
//
//                            intent.hasExtra(terms) -> {
//                                binding?.titlePage?.text = getString(R.string.terms_and_conditions)
//                                binding?.content?.text = HtmlCompat.fromHtml(
//                                    response.settings?.terms ?: "",
//                                    HtmlCompat.FROM_HTML_MODE_LEGACY
//                                )
//                            }
//
//                            intent.hasExtra(privacy) -> {
//                                binding?.titlePage?.text = getString(R.string.privacy_policy)
//                                binding?.content?.text = HtmlCompat.fromHtml(
//                                    response.settings?.privacy ?: "",
//                                    HtmlCompat.FROM_HTML_MODE_LEGACY
//                                )
//                            }
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onError(response: String) {
//            }
//
//        })
//    }

    companion object {
        const val about = "about"
        const val terms = "terms"
        const val privacy = "privacy"
    }
}