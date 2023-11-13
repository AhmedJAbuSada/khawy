package com.khawi.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khawi.data.order.OrderRepository
import com.khawi.data.wallet.WalletRepository
import com.khawi.model.BaseResponse
import com.khawi.model.Order
import com.khawi.model.Wallet
import com.khawi.model.WalletBody
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.model.BaseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    val userMutableLiveData = MutableLiveData<UserModel>()

    private fun getUser() {
        userMutableLiveData.postValue(userRepository.getUser())
    }

    private val _successLiveDataList = MutableLiveData<BaseResponse<MutableList<Wallet>?>?>()
    val successLiveDataList: LiveData<BaseResponse<MutableList<Wallet>?>?> = _successLiveDataList

    private val _successLiveData = MutableLiveData<BaseResponse<Wallet?>?>()
    val successLiveData: LiveData<BaseResponse<Wallet?>?> = _successLiveData

    private val _progressLiveData = MutableLiveData<Boolean>()
    val progressLiveData: MutableLiveData<Boolean> = _progressLiveData

    val params = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            repository.getWalletListFlow().collect {

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
                            _successLiveDataList.postValue(item)
                        }
                    }

                    else -> {
                    }
                }
            }
        }
        viewModelScope.launch {
            repository.getWalletFlow().collect {

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

    suspend fun walletList() {
        _progressLiveData.postValue(true)
        repository.walletList(params as HashMap<String, String>)
    }

    suspend fun addAmount(amount: String) {
        _progressLiveData.postValue(true)
        repository.addAmount(
            WalletBody(
                amount = amount
            )
        )
    }
}