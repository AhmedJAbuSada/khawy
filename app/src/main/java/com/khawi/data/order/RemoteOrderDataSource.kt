package com.khawi.data.order

import com.khawi.model.AddOrderBody
import com.khawi.model.AddRateBody
import com.khawi.model.BaseResponse
import com.khawi.model.ChangeStatusBody
import com.khawi.model.Order
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.RemoteDataSource
import com.khawi.network_base.model.AdvanceResult
import javax.inject.Inject

class RemoteOrderDataSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val repository: UserRepository,
) {

    suspend fun showMap(params: HashMap<String, String>): AdvanceResult<BaseResponse<MutableList<Order>?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.get(
            urlPath = "mobile/order/map",
            headers = header,
            params = params,
        )
    }

    suspend fun orderList(params: HashMap<String, String>): AdvanceResult<BaseResponse<MutableList<Order>?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.get(
            urlPath = "mobile/order/list",
            headers = header,
            params = params,
        )
    }

    suspend fun orderDetails(orderId: String): AdvanceResult<BaseResponse<Order?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.get(
            urlPath = "mobile/order/single/$orderId",
            headers = header,
            params = null,
        )
    }

    suspend fun addOrder(body: AddOrderBody): AdvanceResult<BaseResponse<Order?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/order/add",
            headers = header,
            params = null,
            body = body
        )
    }

    suspend fun addOffer(
        orderId: String,
        body: AddOrderBody
    ): AdvanceResult<BaseResponse<Order?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/order/offer/$orderId",
            headers = header,
            params = null,
            body = body
        )
    }

    suspend fun changeOfferStatusBody(
        orderId: String,
        body: ChangeStatusBody
    ): AdvanceResult<BaseResponse<Order?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/order/offer/update/$orderId",
            headers = header,
            params = null,
            body = body
        )
    }

    suspend fun changeOrderStatusBody(
        orderId: String,
        body: ChangeStatusBody
    ): AdvanceResult<BaseResponse<Order?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/order/update/$orderId",
            headers = header,
            params = null,
            body = body
        )
    }

    suspend fun addRateBody(
        orderId: String,
        body: AddRateBody
    ): AdvanceResult<BaseResponse<Order?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/order/rate/$orderId",
            headers = header,
            params = null,
            body = body
        )
    }

}