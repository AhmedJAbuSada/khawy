package com.khawi.ui.main.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.notification.NotificationRepository
import com.khawi.model.BaseResponse
import com.khawi.model.Notification
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository,
//    private val userRepository: UserRepository,
) : ViewModel() {
//    val userMutableLiveData = MutableLiveData<UserModel>()
//
//    private fun getUser() {
//        userMutableLiveData.postValue(userRepository.getUser())
//    }

    private val _successLiveDataList = MutableLiveData<BaseResponse<MutableList<Notification>?>?>()
    val successLiveDataList: LiveData<BaseResponse<MutableList<Notification>?>?> =
        _successLiveDataList

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    val params = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            repository.getNotificationListFlow().collect {

                when (it) {
                    is BaseState.NetworkError -> {
                        _progressLiveData.postValue(false)
                    }

                    is BaseState.EmptyResult -> {
                        _progressLiveData.postValue(false)
                    }

                    is BaseState.ItemsLoaded -> {
                        it.items?.let { item ->
                            _progressLiveData.postValue(false)
                            _successLiveDataList.postValue(item)
                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }

    fun notificationList() = viewModelScope.launch {
        _progressLiveData.postValue(true)
        repository.notificationList(params as HashMap<String, String>)
    }

    fun notificationRead() = viewModelScope.launch {
        repository.notificationRead()
    }
}