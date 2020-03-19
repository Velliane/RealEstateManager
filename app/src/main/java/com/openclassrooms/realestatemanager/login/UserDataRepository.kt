package com.openclassrooms.realestatemanager.login

import androidx.lifecycle.LiveData

class UserDataRepository(private val userDao: UserDao) {

    suspend fun getAllUsers(): List<User>{
        return userDao.getAllUsers()
    }

    fun addUser(user: User): Long{
        return userDao.addUser(user)
    }

    fun updateUser(user: User): Int{
        return userDao.updateUser(user)
    }

    fun getUserById(userId: String): LiveData<User>{
        return userDao.getUserById(userId)
    }
}