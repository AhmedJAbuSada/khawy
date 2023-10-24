package com.khawi.data.walkthrough

import com.advance.threading.DispatcherProvider
import com.khawi.model.welcome.Welcome
import com.khawi.network_base.model.AdvanceResult
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class WelcomeRepositoryImp @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val remoteWelcomeDataSource: RemoteWelcomeDataSource,
) : WelcomeRepository {

    private val _event =
        MutableStateFlow<BaseState<MutableList<Welcome>>>(BaseState.Idle())

    override suspend fun getWelcome() = withContext(dispatcherProvider.io()) {
        when (val result =
            remoteWelcomeDataSource.getWelcome()) {
            is AdvanceResult.Success -> {
                result.data.data?.let { item ->
                    for (value in item) {
                        value.v = System.currentTimeMillis()
                    }
                    _event.emit(BaseState.ItemsLoaded(item))
                }
                Timber.d("results here res ${result.data}")
            }

            is AdvanceResult.Error -> {
                Timber.d("something went wrong ${result.fault}")
            }

            else -> {

            }
        }
    }

    override suspend fun getWelcomeFlow() = _event.asStateFlow()


}