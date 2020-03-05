package com.openclassrooms.realestatemanager.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.login.UserViewModel
import com.openclassrooms.realestatemanager.add_edit.EditDataViewModel
import com.openclassrooms.realestatemanager.show.MainViewModel
import com.openclassrooms.realestatemanager.show.detail.DetailViewModel
import com.openclassrooms.realestatemanager.show.list.ListViewModel
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class ViewModelFactory(private val context: Context, private val userDataRepository: UserDataRepository, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val firestoreDataRepository: FirestoreDataRepository, private val photoDataRepository: PhotoDataRepository, private val executor: Executor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(context, propertyDataRepository, addressDataRepository, geocodeRepository, firestoreDataRepository, userDataRepository) as T
            }
            modelClass.isAssignableFrom(ListViewModel::class.java) -> {
                ListViewModel(propertyDataRepository, addressDataRepository, geocodeRepository, photoDataRepository) as T
            }
            modelClass.isAssignableFrom(EditDataViewModel::class.java) -> {
                EditDataViewModel(context, photoDataRepository, propertyDataRepository, addressDataRepository, executor) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                return UserViewModel(userDataRepository, executor) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                return DetailViewModel(propertyDataRepository, geocodeRepository, addressDataRepository, photoDataRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel")
        }
    }
}