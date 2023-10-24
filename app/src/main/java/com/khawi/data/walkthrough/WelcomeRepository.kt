package com.khawi.data.walkthrough

import com.khawi.model.Welcome
import com.khawi.network_base.model.BaseState
import kotlinx.coroutines.flow.StateFlow

interface WelcomeRepository {
    suspend fun getWelcome()
    suspend fun getWelcomeFlow(): StateFlow<BaseState<MutableList<Welcome>>>

}