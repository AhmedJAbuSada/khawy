package com.khawi.ui.main.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.order.OrderRepository
import com.khawi.model.BaseResponse
import com.khawi.model.Order
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository,
) : ViewModel() {

    private val _successLiveData = MutableLiveData<BaseResponse<MutableList<Order>?>?>()
    val successLiveData: LiveData<BaseResponse<MutableList<Order>?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    val params = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            repository.getOrderListFlow().collect {

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

    suspend fun orderList() {
        repository.orderList(params as HashMap<String, String>)
    }
}