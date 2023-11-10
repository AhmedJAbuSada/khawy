package com.khawi.ui.request_deliver

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.order.OrderRepository
import com.khawi.model.AddOrderBody
import com.khawi.model.AddRateBody
import com.khawi.model.BaseResponse
import com.khawi.model.Order
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestDeliverViewModel @Inject constructor(
    private val repository: OrderRepository,
) : ViewModel() {

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

    suspend fun addOffer(orderId: String, body: AddOrderBody) {
        repository.addOffer(orderId, body)
    }
}