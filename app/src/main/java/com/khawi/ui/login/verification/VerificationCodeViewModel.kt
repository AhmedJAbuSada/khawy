package com.khawi.ui.login.verification

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
) : ViewModel() {

    private val _successLiveData = MutableLiveData<BaseResponse<UserModel?>?>()
    val successLiveData: LiveData<BaseResponse<UserModel?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    init {
        viewModelScope.launch {
            authRepository.getUserFlow().collect{
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

    suspend fun verifyPhone(code: String) {
        val user = repository.getUser()
        user?.let {
            _progressLiveData.postValue(true)
            authRepository.verifyPhone(it.id, it.phoneNumber ?: "", code)
        }
    }

    suspend fun resendCode(): StateFlow<BaseState<BaseResponse<UserModel?>?>> {
        authRepository.resendCode()
        return authRepository.getUserFlow()
    }

    fun addUser(user: UserModel) {
        repository.insert(user)
    }

}