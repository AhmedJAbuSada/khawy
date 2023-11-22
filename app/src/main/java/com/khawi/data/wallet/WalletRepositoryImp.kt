package com.khawi.data.wallet

import com.advance.threading.DispatcherProvider
import com.khawi.model.AddOrderBody
import com.khawi.model.AddRateBody
import com.khawi.model.BaseResponse
import com.khawi.model.ChangeStatusBody
import com.khawi.model.Coupon
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

class WalletRepositoryImp @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val remoteDataSource: RemoteWalletDataSource,
) : WalletRepository {

    private val _eventCoupon = MutableStateFlow<BaseState<BaseResponse<Coupon?>?>>(BaseState.Idle())
    private val _event = MutableStateFlow<BaseState<BaseResponse<Wallet?>?>>(BaseState.Idle())
    private val _eventList =
        MutableStateFlow<BaseState<BaseResponse<MutableList<Wallet>?>?>>(BaseState.Idle())

    override suspend fun checkCoupon(body: WalletBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.checkCoupon(body)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _eventCoupon.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun addAmount(body: WalletBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.addAmount(body)) {
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

    override suspend fun walletList(params: HashMap<String, String>) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.walletList(params)) {
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

    override suspend fun getCouponFlow() = _eventCoupon.asStateFlow()
    override suspend fun getWalletFlow() = _event.asStateFlow()
    override suspend fun getWalletListFlow() = _eventList.asStateFlow()


}