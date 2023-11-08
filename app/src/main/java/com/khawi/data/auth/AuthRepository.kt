package com.khawi.data.auth

import com.khawi.model.BaseResponse
import com.khawi.model.db.user.UserModel
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface AuthRepository {
    suspend fun loginByPhone(fcmToken:String, phone: String, lat: String, lng: String, address: String)
    suspend fun verifyPhone(id: String, phone: String, code: String)
    suspend fun updateUser(
        id: String,
        email: String? = null,
        image: File? = null,
        name: String? = null,
        phone: String? = null,
        lat: String? = null,
        lng: String? = null,
        address: String? = null,
        hasCar: Boolean? = null,
        carType: String? = null,
        carModel: String? = null,
        carColor: String? = null,
        carNumber: String? = null,
    )
    suspend fun resendCode()
    suspend fun logout()
    suspend fun getUserFlow(): StateFlow<BaseState<BaseResponse<UserModel?>?>>

}