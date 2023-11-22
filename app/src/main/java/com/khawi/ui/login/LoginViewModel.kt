package com.khawi.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    val referralIdLiveData = MutableLiveData<String>()
    val phoneLiveData = MutableLiveData("")
    val imageMutableLiveData = MutableLiveData<File>()

}