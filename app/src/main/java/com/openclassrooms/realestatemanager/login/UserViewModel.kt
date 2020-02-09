package com.openclassrooms.realestatemanager.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.Executor

class UserViewModel(private val userDataRepository: UserDataRepository, private val executor: Executor): ViewModel() {

    fun getAllUsers(): LiveData<List<User>> {
        return  userDataRepository.getAllUsers()
    }

    fun addUser(user: User){
        executor.execute { userDataRepository.addUser(user) }
    }

    fun udpateUser(user: User){
        executor.execute { userDataRepository.updateUser(user) }
    }

    fun getUserById(userId: String): LiveData<User>{
        return userDataRepository.getUserById(userId)
    }
}