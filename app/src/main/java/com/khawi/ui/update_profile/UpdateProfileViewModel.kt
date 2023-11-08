package com.khawi.ui.update_profile

import androidx.lifecycle.MutableLiveData
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
class UpdateProfileViewModel @Inject constructor(
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

    suspend fun updateUser(
        email: String? = null,
        image: File? = null,
        name: String? = null,
        phone: String? = null,
        lat: String? = null,
        lng: String? = null,
        address: String? = null,
        hasCar: Boolean? = null,
        carType: String? = null,
        carModel: String? = null,
        carColor: String? = null,
        carNumber: String? = null,
    ): StateFlow<BaseState<BaseResponse<UserModel?>?>> {
        val user = userMutableLiveData.value
        user?.let {
            authRepository.updateUser(
                it.id,
                email,
                image,
                name,
                phone,
                lat,
                lng,
                address,
                hasCar,
                carType,
                carModel,
                carColor,
                carNumber
            )
        }
        return authRepository.getUserFlow()
    }

    fun addUser(user: UserModel) {
        repository.insert(user)
    }

}