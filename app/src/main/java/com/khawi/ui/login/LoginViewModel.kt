package com.khawi.ui.login

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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
) : ViewModel() {

    val isNewAccount = MutableLiveData<Boolean>()
    val phoneLiveData = MutableLiveData("")
    val usernameLiveData = MutableLiveData("")
    val emailLiveData = MutableLiveData("")

    suspend fun loginByPhone(phone: String): StateFlow<BaseState<BaseResponse<UserModel?>?>> {
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                return@OnCompleteListener
//            }
//
//            val token = task.result
//
//            viewModelScope.launch {
//                authRepository.loginByPhone(token, phone)
//            }
//        })
        viewModelScope.launch {
            authRepository.loginByPhone("token", phone)
        }
        return authRepository.getUserFlow()
    }

    suspend fun verifyPhone(code: String): StateFlow<BaseState<BaseResponse<UserModel?>?>> {
        val user = repository.getUser()
        user?.let {
            authRepository.verifyPhone(it.id, it.phoneNumber ?: "", code)
        }
        return authRepository.getUserFlow()
    }

    suspend fun resendCode(): StateFlow<BaseState<BaseResponse<UserModel?>?>> {
        authRepository.resendCode()
        return authRepository.getUserFlow()
    }

    suspend fun updateUser(
        email: String? = null,
        image: File? = null,
        name: String? = null,
        phone: String? = null,
        type: String? = null
    ): StateFlow<BaseState<BaseResponse<UserModel?>?>> {
        val user = repository.getUser()
        user?.let {
            authRepository.updateUser(it.id, email, image, name, phone, type)
        }
        return authRepository.getUserFlow()
    }

    fun addUser(user: UserModel) {
        repository.insert(user)
    }

}