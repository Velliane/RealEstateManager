package com.openclassrooms.realestatemanager.property.add_edit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class EditDataViewModelFactory (private val context: Context, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val photoDataRepository: PhotoDataRepository, private val executor: Executor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(EditDataViewModel::class.java)){
            return EditDataViewModel(context, photoDataRepository, propertyDataRepository, addressDataRepository, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}