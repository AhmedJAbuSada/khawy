package com.khawi.model.db.user

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getUser(): MutableList<UserModel>?

    @Query("SELECT * FROM user")
    fun getUserFlow(): Flow<MutableList<UserModel>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: UserModel)

    @Query("DELETE FROM user")
    fun deleteAll()
}