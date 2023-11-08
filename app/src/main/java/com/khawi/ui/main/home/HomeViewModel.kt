package com.khawi.ui.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {
    val userMutableLiveData = MutableLiveData<UserModel>()

    init {
        getUser()
    }

    private fun getUser() {
        userMutableLiveData.postValue(repository.getUser())
    }


}