package com.khawi.data.walkthrough

import com.khawi.model.BaseResponse
import com.khawi.model.Welcome
import com.khawi.network_base.RemoteDataSource
import javax.inject.Inject

class RemoteWelcomeDataSource @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun getWelcome() =
        remoteDataSource.get<BaseResponse<MutableList<Welcome>>>(
            urlPath = "mobile/constant/welcome"
        )

}