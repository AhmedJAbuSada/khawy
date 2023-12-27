package com.khawi.data.notification

import com.khawi.model.BaseResponse
import com.khawi.model.Notification
import com.khawi.model.Wallet
import com.khawi.model.WalletBody
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.StateFlow

interface NotificationRepository {
    suspend fun notificationList(params: HashMap<String, String>)
    suspend fun notificationCount()
    suspend fun notificationRead()
    suspend fun getNotificationListFlow(): StateFlow<BaseState<BaseResponse<MutableList<Notification>?>?>>
    suspend fun getNotificationCountFlow(): StateFlow<BaseState<BaseResponse<Int?>?>>

}