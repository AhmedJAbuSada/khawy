package com.khawi.model.db.di

import android.content.Context
import androidx.room.Room
import com.khawi.base.DATABASE_NAME
import com.khawi.model.db.RoomDatabaseBase
import com.khawi.model.db.user.UserModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provide(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, RoomDatabaseBase::class.java, DATABASE_NAME
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideUserDao(db: RoomDatabaseBase) = db.userDao()

    @Provides
    fun provideUserEntity() = UserModel()


}