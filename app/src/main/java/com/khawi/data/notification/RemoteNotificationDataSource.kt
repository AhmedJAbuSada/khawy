package com.khawi.data.notification

import com.khawi.model.BaseResponse
import com.khawi.model.Notification
import com.khawi.model.UserBody
import com.khawi.model.Wallet
import com.khawi.model.WalletBody
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.RemoteDataSource
import com.khawi.network_base.model.AdvanceResult
import javax.inject.Inject

class RemoteNotificationDataSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val repository: UserRepository,
) {

    suspend fun notificationList(params: HashMap<String, String>): AdvanceResult<BaseResponse<MutableList<Notification>?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.get(
            urlPath = "mobile/notification/get",
            headers = header,
            params = params,
        )
    }

    suspend fun notificationCount(): AdvanceResult<BaseResponse<Int?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.get(
            urlPath = "mobile/notification/count",
            headers = header,
            params = null,
        )
    }

    suspend fun notificationRead(): AdvanceResult<BaseResponse<MutableList<Notification>?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/notification/delete",
            headers = header,
            params = null,
            body = UserBody()
        )
    }

}