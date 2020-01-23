package com.openclassrooms.realestatemanager.view_model.injections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class PropertyViewModelFactory(private val propertyDataRepository: PropertyDataRepository, private val executor: Executor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PropertyViewModel::class.java)){
            return PropertyViewModel(propertyDataRepository, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}