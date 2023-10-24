package com.khawi.model.db.user

import javax.inject.Inject

class UserRepository
@Inject constructor(private val dao: UserDao)  {

    fun getUser(): UserModel? {
        return if (dao.getUser() != null && dao.getUser()?.isNotEmpty() == true)
            dao.getUser()?.get(0)
        else
            null
    }

    fun getUserFlow() = dao.getUserFlow()

    fun insert(item: UserModel) {
        dao.deleteAll()
        dao.insert(item)
    }

    fun deleteAll() {
        dao.deleteAll()
    }
}