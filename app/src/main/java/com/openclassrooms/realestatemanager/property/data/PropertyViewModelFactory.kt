package com.openclassrooms.realestatemanager.property.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.property.data.geocode.GeocodeRepository
import com.openclassrooms.realestatemanager.update_database.FirestoreDataRepository
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class PropertyViewModelFactory(private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val firestoreDataRepository: FirestoreDataRepository, private val photoDataRepository: PhotoDataRepository, private val executor: Executor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PropertyViewModel::class.java)){
            return PropertyViewModel(propertyDataRepository, addressDataRepository, geocodeRepository, firestoreDataRepository, photoDataRepository, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}