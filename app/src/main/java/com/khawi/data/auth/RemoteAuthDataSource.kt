package com.khawi.data.auth

import com.khawi.model.BaseResponse
import com.khawi.model.Referral
import com.khawi.model.UserBody
import com.khawi.model.db.user.UserModel
import com.khawi.model.db.user.UserRepository
import com.khawi.network_base.RemoteDataSource
import com.khawi.network_base.model.AdvanceResult
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File
import javax.inject.Inject

class RemoteAuthDataSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val repository: UserRepository,
) {

    suspend fun referral(): AdvanceResult<BaseResponse<Referral?>> {
        val token = repository.getUser()?.token ?: ""
        val header = HashMap<String, String>()
        header["token"] = token
        val body = UserBody()
        return remoteDataSource.post(
            urlPath = "mobile/user/referal",
            headers = header,
            params = null,
            body = body
        )
    }

    suspend fun loginByPhone(
        fcmToken: String,
        phone: String,
        lat: String,
        lng: String,
        address: String,
    ): AdvanceResult<BaseResponse<UserModel?>> {
        val body = UserBody(
            phoneNumber = phone,
            os = "Android",
            fcmToken = fcmToken,
            lat = lat,
            lng = lng,
            address = address,
        )
        return remoteDataSource.post(
            urlPath = "mobile/user/create_login",
            params = null,
            body = body
        )
    }

    suspend fun verifyPhone(
        id: String,
        phone: String,
        code: String,
        by: String?,
    ): AdvanceResult<BaseResponse<UserModel?>> {
        val body = UserBody(
            phoneNumber = phone,
            id = id,
            verifyCode = code,
            by = by,
        )
        return remoteDataSource.post(
            urlPath = "mobile/user/verify",
            params = null,
            body = body
        )
    }

    suspend fun resendCode(): AdvanceResult<BaseResponse<UserModel?>> {
        val body = UserBody(
            _id = repository.getUser()?.id ?: "",
        )
        return remoteDataSource.post(
            urlPath = "mobile/user/resend",
            params = null,
            body = body
        )
    }

    suspend fun logout(): AdvanceResult<BaseResponse<UserModel?>> {
        return remoteDataSource.post(
            urlPath = "mobile/user/logout/${repository.getUser()?.id ?: ""}",
            params = null,
            body = UserBody()
        )
    }

    suspend fun updateUser(
        id: String,
        email: String? = null,
        image: File? = null,
        name: String? = null,
        phoneNumber: String? = null,
        lat: String? = null,
        lng: String? = null,
        address: String? = null,
        hasCar: Boolean? = null,
        carType: String? = null,
        carModel: String? = null,
        carColor: String? = null,
        carNumber: String? = null,
    ): AdvanceResult<BaseResponse<UserModel?>> {
        val token = repository.getUser()?.token ?: ""
        val phone = phoneNumber ?: (repository.getUser()?.phoneNumber ?: "")
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/user/update-profile",
            headers = header,
            files = formData {
                append("_id", id)
                append("phone_number", phone)
                email?.let {
                    append("email", it)
                }
                image?.let {
                    append("image", it.readBytes(), Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=${it.name}")
                    })
                }
                name?.let {
                    append("full_name", it)
                }
                lat?.let {
                    append("lat", lat)
                }
                lng?.let {
                    append("lng", lng)
                }
                address?.let {
                    append("address", address)
                }
                hasCar?.let {
                    append("hasCar", hasCar)
                }
                carType?.let {
                    append("carType", carType)
                }
                carModel?.let {
                    append("carModel", carModel)
                }
                carColor?.let {
                    append("carColor", carColor)
                }
                carNumber?.let {
                    append("carNumber", carNumber)
                }
            })


    }

}