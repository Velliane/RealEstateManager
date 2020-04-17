package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.data.database.UserDao
import com.openclassrooms.realestatemanager.login.User


open class UserDataRepository(private val userDao: UserDao) {

    suspend fun getAllUsers(): List<User>{
        return userDao.getAllUsers()
    }

    fun addUser(user: User): Long{
        return userDao.addUser(user)
    }

    fun updateUser(user: User): Int{
        return userDao.updateUser(user)
    }

    open suspend fun getUserById(userId: String): User {
        return userDao.getUserById(userId)
    }
}