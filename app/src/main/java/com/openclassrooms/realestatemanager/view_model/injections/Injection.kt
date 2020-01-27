package com.openclassrooms.realestatemanager.view_model.injections

import android.content.Context
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.database.repositories.AddressDataRepository
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.database.repositories.UserDataRepository
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
            return PropertyViewModelFactory(propertyDataRepository, addressDataRepository,  executor)
        }

        fun provideUserViewModelFactory(context: Context): UserViewModelFactory {
            val userDataRepository = provideUserDataSource(context)
            val executor = provideExecutor()
            return UserViewModelFactory(userDataRepository, executor)
        }
    }
}