package com.khawi.data.wallet

import com.khawi.model.BaseResponse
import com.khawi.model.Wallet
import com.khawi.model.WalletBody
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.RemoteDataSource
import com.khawi.network_base.model.AdvanceResult
import javax.inject.Inject

class RemoteWalletDataSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val repository: UserRepository,
) {

    suspend fun walletList(params: HashMap<String, String>): AdvanceResult<BaseResponse<MutableList<Wallet>?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.get(
            urlPath = "mobile/transaction/list",
            headers = header,
            params = params,
        )
    }

    suspend fun addAmount(body: WalletBody): AdvanceResult<BaseResponse<Wallet?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/user/wallet",
            headers = header,
            params = null,
            body = body
        )
    }

}