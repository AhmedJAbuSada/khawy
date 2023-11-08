package com.khawi.ui.login.phone_number

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
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
class PhoneNumberViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
) : ViewModel() {

    suspend fun loginByPhone(
        phone: String,
        lat: String,
        lng: String,
        address: String,
    ): StateFlow<BaseState<BaseResponse<UserModel?>?>> {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result

            viewModelScope.launch {
                authRepository.loginByPhone(token, phone, lat, lng,address)
            }
        })
        return authRepository.getUserFlow()
    }

    fun addUser(user: UserModel) {
        repository.insert(user)
    }

}