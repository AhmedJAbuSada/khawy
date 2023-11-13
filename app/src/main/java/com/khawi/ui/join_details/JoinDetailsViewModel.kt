package com.khawi.ui.join_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.order.OrderRepository
import com.khawi.model.AddRateBody
import com.khawi.model.BaseResponse
import com.khawi.model.ChangeStatusBody
import com.khawi.model.Order
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinDetailsViewModel @Inject constructor(
    private val repository: OrderRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val userMutableLiveData = MutableLiveData<UserModel>()

    private fun getUser() {
        userMutableLiveData.postValue(userRepository.getUser())
    }

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
        viewModelScope.launch {
            getUser()
        }
    }

    suspend fun changeOfferStatusBody(offerId: String, status: String, note: String? = null) {
        _progressLiveData.postValue(true)
        repository.changeOfferStatusBody(
            offerId, ChangeStatusBody(
                status = status,
                canceledNote = note
            )
        )
    }
}