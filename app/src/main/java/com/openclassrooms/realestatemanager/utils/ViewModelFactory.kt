package com.openclassrooms.realestatemanager.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.login.UserViewModel
import com.openclassrooms.realestatemanager.add_edit.EditDataViewModel
import com.openclassrooms.realestatemanager.search.SearchViewModel
import com.openclassrooms.realestatemanager.settings.SettingsViewModel
import com.openclassrooms.realestatemanager.show.MainViewModel
import com.openclassrooms.realestatemanager.show.detail.DetailViewModel
import com.openclassrooms.realestatemanager.show.list.ListViewModel
import com.openclassrooms.realestatemanager.show.map.MapViewModel
import com.openclassrooms.realestatemanager.simulator.SimulatorViewModel
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor

class ViewModelFactory(private val context: Context, private val userDataRepository: UserDataRepository, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val firestoreDataRepository: FirestoreDataRepository, private val photoDataRepository: PhotoDataRepository, private val executor: Executor): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(AuthUI.getInstance(), context, propertyDataRepository, addressDataRepository, firestoreDataRepository, userDataRepository) as T
            }
            modelClass.isAssignableFrom(ListViewModel::class.java) -> {
                ListViewModel(context, propertyDataRepository, addressDataRepository, photoDataRepository) as T
            }
            modelClass.isAssignableFrom(EditDataViewModel::class.java) -> {
                EditDataViewModel(FirebaseAuth.getInstance(), context, photoDataRepository, propertyDataRepository, addressDataRepository, executor, userDataRepository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                return UserViewModel(userDataRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                return DetailViewModel(propertyDataRepository, geocodeRepository, addressDataRepository, photoDataRepository, userDataRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(propertyDataRepository, addressDataRepository, geocodeRepository) as T
            }
            modelClass.isAssignableFrom(SimulatorViewModel::class.java) -> {
                SimulatorViewModel() as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(context) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(context, userDataRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel")
        }
    }
}