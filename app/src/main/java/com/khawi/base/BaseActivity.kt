package com.khawi.base

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale


open class BaseActivity : AppCompatActivity() {
    private var language = ""

    override fun onResume() {
        super.onResume()
        try {
            hideKeyboard()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        language = this@BaseActivity.getPreference(language_key)
        if (language.isEmpty()) {
            language = english_key
        }
        setLocale(Locale(language))
    }

    override fun attachBaseContext(base: Context) {
        var language = base.getPreference(language_key)
        if (language.isEmpty()) {
            language = english_key
        }
        super.attachBaseContext(ContextWrapper(base).wrap(language))
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        var language = getPreference(language_key)
        if (language.isEmpty()) {
            language = english_key
        }
        val locale = Locale(language)
        overrideConfiguration?.setLocale(locale)
        super.applyOverrideConfiguration(overrideConfiguration)
    }

}