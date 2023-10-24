package com.khawi.base.threading.di

import com.advance.threading.DefaultDispatcherProvider
import com.advance.threading.DispatcherProvider
import dagger.Binds
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@dagger.Module
@InstallIn(SingletonComponent::class)
abstract class ThreadingModule {

    @Binds
    abstract fun bindThreadingModule(defaultDispatcherProvider: DefaultDispatcherProvider):
            DispatcherProvider

}