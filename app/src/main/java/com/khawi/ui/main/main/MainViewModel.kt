package com.khawi.ui.main.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.notification.NotificationRepository
import com.khawi.model.BaseResponse
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository,
    private val repositoryNotification: NotificationRepository,
) : ViewModel() {

    private val _successLiveData = MutableLiveData<BaseResponse<Int?>?>()
    val successLiveData: LiveData<BaseResponse<Int?>?> = _successLiveData

    val userMutableLiveData = MutableLiveData<UserModel>()

    fun getUser() {
        userMutableLiveData.postValue(repository.getUser())
    }

    init {
        viewModelScope.launch {
            repositoryNotification.getNotificationCountFlow().collect {

                when (it) {
                    is BaseState.NetworkError -> {
                    }

                    is BaseState.EmptyResult -> {
                    }

                    is BaseState.ItemsLoaded -> {
                        it.items?.let { item ->
                            _successLiveData.postValue(item)
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }

    fun notificationCount() = viewModelScope.launch {
        repositoryNotification.notificationCount()
    }

}