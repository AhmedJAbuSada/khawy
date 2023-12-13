package com.khawi.ui.main.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {
//    val imageMutableLiveData = MutableLiveData<File>()

    val userMutableLiveData = MutableLiveData<UserModel>()

    fun getUser() {
        userMutableLiveData.postValue(repository.getUser())
    }


}