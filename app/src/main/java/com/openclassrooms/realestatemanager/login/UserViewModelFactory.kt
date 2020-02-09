package com.openclassrooms.realestatemanager.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class UserViewModelFactory(private val userDataRepository: UserDataRepository, private val executor: Executor) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userDataRepository, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
