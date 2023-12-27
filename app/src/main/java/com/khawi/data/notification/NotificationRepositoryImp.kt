package com.khawi.data.notification

import com.advance.threading.DispatcherProvider
import com.khawi.model.AddOrderBody
import com.khawi.model.AddRateBody
import com.khawi.model.BaseResponse
import com.khawi.model.ChangeStatusBody
import com.khawi.model.Notification
import com.khawi.model.Order
import com.khawi.model.Wallet
import com.khawi.model.WalletBody
import com.khawi.network_base.model.AdvanceResult
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class NotificationRepositoryImp @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val remoteDataSource: RemoteNotificationDataSource,
) : NotificationRepository {

    private val _eventList =
        MutableStateFlow<BaseState<BaseResponse<MutableList<Notification>?>?>>(BaseState.Idle())
    private val _eventCount =
        MutableStateFlow<BaseState<BaseResponse<Int?>?>>(BaseState.Idle())

    override suspend fun notificationList(params: HashMap<String, String>) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.notificationList(params)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _eventList.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun notificationRead() =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.notificationRead()) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _eventList.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun notificationCount() =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.notificationCount()) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _eventCount.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun getNotificationListFlow() = _eventList.asStateFlow()
    override suspend fun getNotificationCountFlow() = _eventCount.asStateFlow()


}