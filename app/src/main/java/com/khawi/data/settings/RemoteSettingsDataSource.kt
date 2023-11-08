package com.khawi.data.settings

import com.khawi.model.BaseResponse
import com.khawi.model.ContactUsBody
import com.khawi.model.StaticPage
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

class RemoteSettingsDataSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val repository: UserRepository,
) {

    suspend fun contactUs(body: ContactUsBody): AdvanceResult<BaseResponse<ContactUsBody?>> {
        return remoteDataSource.post(
            urlPath = "mobile/constant/add-complain",
            params = null,
            body = body
        )
    }

    suspend fun getStaticPages(): AdvanceResult<BaseResponse<MutableList<StaticPage>?>> {
        return remoteDataSource.post(
            urlPath = "mobile/constant/static",
            params = null,
            body = null
        )
    }

}