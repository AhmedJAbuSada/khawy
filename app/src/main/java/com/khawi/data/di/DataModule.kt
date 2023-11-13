package com.khawi.data.di

import com.khawi.data.auth.AuthRepository
import com.khawi.data.auth.AuthRepositoryImp
import com.khawi.data.notification.NotificationRepository
import com.khawi.data.notification.NotificationRepositoryImp
import com.khawi.data.order.OrderRepository
import com.khawi.data.order.OrderRepositoryImp
import com.khawi.data.settings.SettingsRepository
import com.khawi.data.settings.SettingsRepositoryImp
import com.khawi.data.walkthrough.WelcomeRepository
import com.khawi.data.walkthrough.WelcomeRepositoryImp
import com.khawi.data.wallet.WalletRepository
import com.khawi.data.wallet.WalletRepositoryImp
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

    @Binds
    fun bindsSettingsRepository(
        settingsRepositoryImp: SettingsRepositoryImp,
    ): SettingsRepository

    @Binds
    fun bindsOrderRepository(
        orderRepositoryImp: OrderRepositoryImp,
    ): OrderRepository

    @Binds
    fun bindsWalletRepository(
        walletRepositoryImp: WalletRepositoryImp,
    ): WalletRepository

    @Binds
    fun bindsNotificationRepository(
        notificationRepositoryImp: NotificationRepositoryImp,
    ): NotificationRepository

}
