package com.khawi.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.order.OrderRepository
import com.khawi.model.AddOrderBody
import com.khawi.model.BaseResponse
import com.khawi.model.Order
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: OrderRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val userMutableLiveData = MutableLiveData<UserModel>()

    init {
        getUser()
    }

    private fun getUser() {
        userMutableLiveData.postValue(userRepository.getUser())
    }

    private val _successLiveData = MutableLiveData<BaseResponse<MutableList<Order>?>?>()
    val successLiveData: LiveData<BaseResponse<MutableList<Order>?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

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

    suspend fun getOrders(lat: String, lng: String, address: String? = null) {
        val params = HashMap<String, String>()
        params["lat"] = lat
        params["lng"] = lng
        if (address != null)
            params["address"] = address
        repository.showMap(params)
    }
}