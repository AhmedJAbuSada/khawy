package com.khawi.ui.contact_us

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.khawi.data.auth.AuthRepository
import com.khawi.data.settings.SettingsRepository
import com.khawi.model.BaseResponse
import com.khawi.model.ContactUsBody
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactUSViewModel @Inject constructor(
    private val repository: SettingsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val userMutableLiveData = MutableLiveData<UserModel>()

    private fun getUser() {
        userMutableLiveData.postValue(userRepository.getUser())
    }

    private val _successLiveData = MutableLiveData<BaseResponse<ContactUsBody?>?>()
    val successLiveData: LiveData<BaseResponse<ContactUsBody?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    init {
        viewModelScope.launch {
            getUser()
        }
        viewModelScope.launch {
            repository.getContactUsFlow().collect {

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

    suspend fun contactUs(
        details: String,
        fullName: String,
        email: String,
        phoneNumber: String,
    ) {
        _progressLiveData.postValue(true)
        repository.contactUs(
            ContactUsBody(
                details = details,
                fullName = fullName,
                email = email,
                phoneNumber = phoneNumber,
            )
        )
    }

}