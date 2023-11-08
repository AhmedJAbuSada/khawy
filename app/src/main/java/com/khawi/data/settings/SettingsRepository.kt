package com.khawi.data.settings

import com.khawi.model.BaseResponse
import com.khawi.model.ContactUsBody
import com.khawi.model.StaticPage
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    suspend fun contactUs(body: ContactUsBody)
    suspend fun getStaticPages()
    suspend fun getResponseFlow(): StateFlow<BaseState<BaseResponse<MutableList<StaticPage>?>?>>
    suspend fun getContactUsFlow(): StateFlow<BaseState<BaseResponse<ContactUsBody?>?>>

}