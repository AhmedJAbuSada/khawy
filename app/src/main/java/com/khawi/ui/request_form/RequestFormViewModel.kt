package com.khawi.ui.request_form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.khawi.data.auth.AuthRepository
import com.khawi.data.order.OrderRepository
import com.khawi.data.settings.SettingsRepository
import com.khawi.model.AddOrderBody
import com.khawi.model.BaseResponse
import com.khawi.model.ContactUsBody
import com.khawi.model.Order
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestFormViewModel @Inject constructor(private val repository: OrderRepository) :
    ViewModel() {

    private val _successLiveData = MutableLiveData<BaseResponse<Order?>?>()
    val successLiveData: LiveData<BaseResponse<Order?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    init {
        viewModelScope.launch {
            repository.getOrderFlow().collect {
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

    suspend fun addOrder(body: AddOrderBody) {
        _progressLiveData.postValue(true)
        repository.addOrder(body)
    }

}