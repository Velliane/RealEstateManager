package com.openclassrooms.realestatemanager.utils

import android.content.Context
import com.openclassrooms.realestatemanager.property.data.PropertyDatabase
import com.openclassrooms.realestatemanager.property.data.AddressDataRepository
import com.openclassrooms.realestatemanager.property.data.geocode.GeocodeRepository
import com.openclassrooms.realestatemanager.property.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.login.UserViewModelFactory
import com.openclassrooms.realestatemanager.property.data.PropertyViewModelFactory
import com.openclassrooms.realestatemanager.update_database.FirestoreDataRepository
import com.openclassrooms.realestatemanager.update_database.FirestoreDataViewModelFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Injection {

    companion object{

        private fun providePropertyDataSource(context: Context): PropertyDataRepository {
            val database = PropertyDatabase.getInstance(context)
            return PropertyDataRepository(database.propertyDao())
        }

        private fun provideUserDataSource(context: Context) : UserDataRepository {
            val database = PropertyDatabase.getInstance(context)
            return UserDataRepository(database.userDao())
        }

        private fun provideAddressDataSource(context: Context): AddressDataRepository {
            val database = PropertyDatabase.getInstance(context)
            return AddressDataRepository(database.addressDao())
        }


        private fun provideExecutor(): Executor {
            return Executors.newSingleThreadExecutor()
        }

        fun providePropertyViewModelFactory(context: Context): PropertyViewModelFactory {
            val propertyDataRepository = providePropertyDataSource(context)
            val addressDataRepository = provideAddressDataSource(context)
            val executor = provideExecutor()
            val geocodeRepository = GeocodeRepository()
            return PropertyViewModelFactory(propertyDataRepository, addressDataRepository, geocodeRepository, executor)
        }

        fun provideUserViewModelFactory(context: Context): UserViewModelFactory {
            val userDataRepository = provideUserDataSource(context)
            val executor = provideExecutor()
            return UserViewModelFactory(userDataRepository, executor)
        }

        fun provideFirestoreDataViewModelFactory(): FirestoreDataViewModelFactory {
            val firestoreDataRepository = FirestoreDataRepository()
            return  FirestoreDataViewModelFactory(firestoreDataRepository)
        }
    }
}