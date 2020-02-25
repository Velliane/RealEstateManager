package com.openclassrooms.realestatemanager.property.show

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.property.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class MainViewModelFactory(private val context: Context, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val firestoreDataRepository: FirestoreDataRepository, private val photoDataRepository: PhotoDataRepository, private val executor: Executor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(context, propertyDataRepository, addressDataRepository, geocodeRepository, firestoreDataRepository, photoDataRepository, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}