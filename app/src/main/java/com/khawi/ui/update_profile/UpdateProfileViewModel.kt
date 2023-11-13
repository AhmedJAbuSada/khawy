package com.khawi.ui.update_profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.auth.AuthRepository
import com.khawi.model.BaseResponse
import com.khawi.model.ContactUsBody
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repository: UserRepository,
) : ViewModel() {
    val userMutableLiveData = MutableLiveData<UserModel>()

    private val _successLiveData = MutableLiveData<BaseResponse<UserModel?>?>()
    val successLiveData: LiveData<BaseResponse<UserModel?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    init {
        viewModelScope.launch {
            getUser()
        }
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
    ) {
        _progressLiveData.postValue(true)
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
    }

    fun addUser(user: UserModel) {
        repository.insert(user)
    }

}