package com.openclassrooms.realestatemanager.utils

import android.content.Context
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.property.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.login.UserViewModelFactory
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.property.add_edit.EditDataViewModelFactory
import com.openclassrooms.realestatemanager.property.show.MainViewModelFactory
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.database.PropertyDao
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

        private fun provideFirestoreDataSource(context: Context): FirestoreDataRepository{
            val database = PropertyDatabase.getInstance(context)
            return FirestoreDataRepository(database.propertyDao(), database.addressDao())
        }


        private fun provideExecutor(): Executor {
            return Executors.newSingleThreadExecutor()
        }

        fun provideMainViewModelFactory(context: Context): MainViewModelFactory {
            val propertyDataRepository = providePropertyDataSource(context)
            val addressDataRepository = provideAddressDataSource(context)
            val executor = provideExecutor()
            val geocodeRepository = GeocodeRepository()
            val firestoreDataRepository = provideFirestoreDataSource(context)
            val photoDataRepository = PhotoDataRepository()
            return MainViewModelFactory(context, propertyDataRepository, addressDataRepository, geocodeRepository, firestoreDataRepository, photoDataRepository, executor)
        }

        fun provideUserViewModelFactory(context: Context): UserViewModelFactory {
            val userDataRepository = provideUserDataSource(context)
            val executor = provideExecutor()
            return UserViewModelFactory(userDataRepository, executor)
        }

        fun provideEditDataViewModelFactory(context: Context): EditDataViewModelFactory {
            val propertyDataRepository = providePropertyDataSource(context)
            val addressDataRepository = provideAddressDataSource(context)
            val executor = provideExecutor()
            val photoDataRepository = PhotoDataRepository()
            return EditDataViewModelFactory(context, propertyDataRepository, addressDataRepository, photoDataRepository, executor)
        }

    }
}