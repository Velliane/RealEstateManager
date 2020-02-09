package com.openclassrooms.realestatemanager.login

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.login.UserDao
import com.openclassrooms.realestatemanager.login.User

class UserDataRepository(private val userDao: UserDao) {

    fun getAllUsers(): LiveData<List<User>>{
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