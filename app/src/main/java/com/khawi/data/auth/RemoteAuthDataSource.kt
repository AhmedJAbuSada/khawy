package com.khawi.data.auth

import com.khawi.model.BaseResponse
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

    suspend fun loginByPhone(
        fcmToken: String,
        phone: String
    ): AdvanceResult<BaseResponse<UserModel?>> {
        val body = UserBody(
            phoneNumber = phone,
            os = "Android",
            fcmToken = fcmToken,
            lat = "32.292929",
            lng = "41.231231",
        )
        return remoteDataSource.post(
            urlPath = "mobile/user/add",
            params = null,
            body = body
        )
    }

    suspend fun verifyPhone(
        id: String,
        phone: String,
        code: String
    ): AdvanceResult<BaseResponse<MutableList<UserModel>?>> {
        val body = UserBody(
            phoneNumber = phone,
            id = id,
            verifyCode = code,
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

    suspend fun updateUser(
        id: String,
        email: String? = null,
        image: File? = null,
        name: String? = null,
        phoneNumber: String? = null,
        type: String? = null,
    ): AdvanceResult<BaseResponse<UserModel?>> {
        val token = repository.getUser()?.token ?: ""
        val phone = phoneNumber ?: (repository.getUser()?.phoneNumber ?: "")
        val header = HashMap<String, String>()
        header["token"] = token
        return remoteDataSource.post(
            urlPath = "mobile/user/update",
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
                    append("name", it)
                }
//                phone?.let {
//                    append("phone_number", it)
//                }
                type?.let {
                    append("type", it)
                }
            })


    }

}