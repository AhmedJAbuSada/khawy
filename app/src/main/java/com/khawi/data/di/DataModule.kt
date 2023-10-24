package com.khawi.data.di

import com.khawi.data.auth.AuthRepository
import com.khawi.data.auth.AuthRepositoryImp
import com.khawi.data.walkthrough.WelcomeRepository
import com.khawi.data.walkthrough.WelcomeRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsWelcomeRepository(
        welcomeRepositoryImp: WelcomeRepositoryImp,
    ): WelcomeRepository

    @Binds
    fun bindsAuthRepository(
        authRepositoryImp: AuthRepositoryImp,
    ): AuthRepository

}
