package com.khawi.data.auth

import com.advance.threading.DispatcherProvider
import com.khawi.model.BaseResponse
import com.khawi.model.Referral
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
    private val _eventReferral =
        MutableStateFlow<BaseState<BaseResponse<Referral?>?>>(BaseState.Idle())

    override suspend fun referral() =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.referral()) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _eventReferral.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun loginByPhone(
        fcmToken: String,
        phone: String,
        lat: String,
        lng: String,
        address: String,
    ) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.loginByPhone(fcmToken, phone, lat, lng, address)) {
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

    override suspend fun verifyPhone(id: String, phone: String, code: String, by: String?) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.verifyPhone(id, phone, code, by)) {
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

    override suspend fun logout() =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.logout()) {
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
        lat: String?,
        lng: String?,
        address: String?,
        hasCar: Boolean?,
        carType: String?,
        carModel: String?,
        carColor: String?,
        carNumber: String?,
        identityImageFile: File?,
        licenseImageFile: File?,
        carFrontImageFile: File?,
        carBackImageFile: File?,
        carRightImageFile: File?,
        carLeftImageFile: File?,
    ) = withContext(dispatcherProvider.io()) {
        when (val result =
            remoteDataSource.updateUser(
                id,
                email,
                image,
                name,
                phone,
                lat,
                lng,
                address,
                hasCar,
                carType,
                carModel,
                carColor,
                carNumber,
                identityImageFile,
                licenseImageFile,
                carFrontImageFile,
                carBackImageFile,
                carRightImageFile,
                carLeftImageFile,
            )) {
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
    override suspend fun getReferralFlow() = _eventReferral.asStateFlow()


}