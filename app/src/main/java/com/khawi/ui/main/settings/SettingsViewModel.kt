package com.khawi.ui.main.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.auth.AuthRepository
import com.khawi.model.BaseResponse
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
) : ViewModel() {
    val userMutableLiveData = MutableLiveData<UserModel>()

    init {
        getUser()
    }

    private fun getUser() {
        userMutableLiveData.postValue(repository.getUser())
    }

    private val _successLiveData = MutableLiveData<BaseResponse<UserModel?>?>()
    val successLiveData: LiveData<BaseResponse<UserModel?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    init {
        viewModelScope.launch {
            authRepository.getUserFlow().collect {
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
                            _successLiveData.postValue(item)

                        }
                    }

                    else -> {
                    }
                }
            }
        }
    }

    suspend fun logout() {
        _progressLiveData.postValue(true)
        authRepository.logout()
    }

    fun deleteAll() {
        repository.deleteAll()
    }


}