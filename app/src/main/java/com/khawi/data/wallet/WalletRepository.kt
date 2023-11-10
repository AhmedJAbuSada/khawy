package com.khawi.data.wallet

import com.khawi.model.BaseResponse
import com.khawi.model.Wallet
import com.khawi.model.WalletBody
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.StateFlow

interface WalletRepository {
    suspend fun addAmount(body: WalletBody)
    suspend fun walletList(params: HashMap<String, String>)
    suspend fun getWalletFlow(): StateFlow<BaseState<BaseResponse<Wallet?>?>>
    suspend fun getWalletListFlow(): StateFlow<BaseState<BaseResponse<MutableList<Wallet>?>?>>

}