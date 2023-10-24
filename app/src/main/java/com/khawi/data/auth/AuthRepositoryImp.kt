package com.khawi.data.auth

import com.advance.threading.DispatcherProvider
import com.khawi.model.BaseResponse
import com.khawi.model.db.user.UserModel
import com.khawi.network_base.model.AdvanceResult
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class AuthRepositoryImp @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val remoteDataSource: RemoteAuthDataSource,
) : AuthRepository {

    private val _event = MutableStateFlow<BaseState<BaseResponse<UserModel?>?>>(BaseState.Idle())

    override suspend fun loginByPhone(fcmToken: String, phone: String) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.loginByPhone(fcmToken, phone)) {
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

    override suspend fun verifyPhone(id: String, phone: String, code: String) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.verifyPhone(id, phone, code)) {
                is AdvanceResult.Success -> {
                    if (result.data.data?.isNotEmpty() == true) {
                        val response = BaseResponse<UserModel?>()
                        response.data = result.data.data?.get(0)
                        response.status = result.data.status
                        response.message = result.data.message
                        response.v = System.currentTimeMillis()
                        _event.emit(BaseState.ItemsLoaded(response))
                    }
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun resendCode() =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.resendCode()) {
                is AdvanceResult.Success -> {
                    _event.emit(BaseState.ItemsLoaded(result.data))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun updateUser(
        id: String,
        email: String?,
        image: File?,
        name: String?,
        phone: String?,
        type: String?,
    ) = withContext(dispatcherProvider.io()) {
        when (val result =
            remoteDataSource.updateUser(id, email, image, name, phone, type)) {
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

    override suspend fun getUserFlow() = _event.asStateFlow()


}