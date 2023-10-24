package com.khawi.ui.login.verification

import androidx.lifecycle.ViewModel
import com.khawi.data.auth.AuthRepository
import com.khawi.model.BaseResponse
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class VerificationCodeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
) : ViewModel() {

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

    fun addUser(user: UserModel) {
        repository.insert(user)
    }

}