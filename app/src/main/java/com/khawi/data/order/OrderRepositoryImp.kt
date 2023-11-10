package com.khawi.data.order

import com.advance.threading.DispatcherProvider
import com.khawi.model.AddOrderBody
import com.khawi.model.AddRateBody
import com.khawi.model.BaseResponse
import com.khawi.model.ChangeStatusBody
import com.khawi.model.Order
import com.khawi.network_base.model.AdvanceResult
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class OrderRepositoryImp @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val remoteDataSource: RemoteOrderDataSource,
) : OrderRepository {

    private val _event = MutableStateFlow<BaseState<BaseResponse<Order?>?>>(BaseState.Idle())
    private val _eventList =
        MutableStateFlow<BaseState<BaseResponse<MutableList<Order>?>?>>(BaseState.Idle())

    override suspend fun addOrder(body: AddOrderBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.addOrder(body)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _event.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun addOffer(orderId: String, body: AddOrderBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.addOffer(orderId, body)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _event.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun changeOfferStatusBody(offerId: String, body: ChangeStatusBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.changeOfferStatusBody(offerId, body)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _event.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun changeOrderStatusBody(orderId: String, body: ChangeStatusBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.changeOrderStatusBody(orderId, body)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _event.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun addRateBody(orderId: String, body: AddRateBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.addRateBody(orderId, body)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _event.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun showMap(params: HashMap<String, String>) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.showMap(params)) {
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

    override suspend fun orderList(params: HashMap<String, String>) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.orderList(params)) {
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

    override suspend fun orderDetails(orderId: String) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.orderDetails(orderId)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _event.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun getOrderFlow() = _event.asStateFlow()
    override suspend fun getOrderListFlow() = _eventList.asStateFlow()


}