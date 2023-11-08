package com.khawi.data.settings

import com.advance.threading.DispatcherProvider
import com.khawi.model.BaseResponse
import com.khawi.model.ContactUsBody
import com.khawi.model.StaticPage
import com.khawi.model.db.user.UserModel
import com.khawi.network_base.model.AdvanceResult
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class SettingsRepositoryImp @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val remoteDataSource: RemoteSettingsDataSource,
) : SettingsRepository {

    private val _event = MutableStateFlow<BaseState<BaseResponse<MutableList<StaticPage>?>?>>(BaseState.Idle())
    private val _eventContactUs = MutableStateFlow<BaseState<BaseResponse<ContactUsBody?>?>>(BaseState.Idle())

    override suspend fun contactUs(body: ContactUsBody) =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.contactUs(body)) {
                is AdvanceResult.Success -> {
                    val item = result.data
                    item.v = System.currentTimeMillis()
                    _eventContactUs.emit(BaseState.ItemsLoaded(item))
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun getStaticPages() =
        withContext(dispatcherProvider.io()) {
            when (val result = remoteDataSource.getStaticPages()) {
                is AdvanceResult.Success -> {
                    if (result.data.data?.isNotEmpty() == true) {
                        val item = result.data
                        item.v = System.currentTimeMillis()
                        _event.emit(BaseState.ItemsLoaded(item))
                    }
                    Timber.d("results here res ${result.data}")
                }

                is AdvanceResult.Error -> {
                    Timber.d("something went wrong ${result.fault}")
                }
            }
        }

    override suspend fun getResponseFlow() = _event.asStateFlow()
    override suspend fun getContactUsFlow() = _eventContactUs.asStateFlow()


}