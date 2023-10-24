package com.khawi.ui.login.username

import androidx.lifecycle.ViewModel
import com.khawi.data.auth.AuthRepository
import com.khawi.model.BaseResponse
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
) : ViewModel() {


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