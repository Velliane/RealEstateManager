package com.openclassrooms.realestatemanager.view_model.injections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.database.repositories.UserDataRepository
import com.openclassrooms.realestatemanager.view_model.UserViewModel
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
