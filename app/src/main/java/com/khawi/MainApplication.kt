package com.khawi

import androidx.multidex.MultiDexApplication
import com.khawi.base.english_key
import com.khawi.base.getPreference
import com.khawi.base.language_key
import com.khawi.base.setLocale
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class MainApplication : MultiDexApplication() {

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        var language = getPreference(language_key)
        if (language.isEmpty()) {
            language = english_key
        }
        setLocale(Locale(language))

//        FirebaseApp.initializeApp(this)
    }

    companion object {
        private var instance: MainApplication? = null

        fun applicationContext(): MainApplication {
            return instance as MainApplication
        }
    }

}