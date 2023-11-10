package com.khawi.data.order

import com.khawi.model.AddOrderBody
import com.khawi.model.AddRateBody
import com.khawi.model.BaseResponse
import com.khawi.model.ChangeStatusBody
import com.khawi.model.Order
import com.khawi.model.db.user.UserModel
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface OrderRepository {
    suspend fun addOrder(body: AddOrderBody)
    suspend fun addOffer(orderId: String, body: AddOrderBody)
    suspend fun changeOfferStatusBody(offerId: String, body: ChangeStatusBody)
    suspend fun changeOrderStatusBody(orderId: String, body: ChangeStatusBody)
    suspend fun addRateBody(orderId: String, body: AddRateBody)
    suspend fun showMap(params: HashMap<String, String>)
    suspend fun orderList(params: HashMap<String, String>)
    suspend fun orderDetails(orderId: String)
    suspend fun getOrderFlow(): StateFlow<BaseState<BaseResponse<Order?>?>>
    suspend fun getOrderListFlow(): StateFlow<BaseState<BaseResponse<MutableList<Order>?>?>>

}