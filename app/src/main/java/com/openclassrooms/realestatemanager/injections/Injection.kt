package com.openclassrooms.realestatemanager.injections

import android.content.Context
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class Injection {

    companion object{

        private fun providePropertyDataSource(context: Context): PropertyDataRepository {
            val database = PropertyDatabase.getInstance(context)
            return PropertyDataRepository(database.propertyDao())
        }

        private fun provideExecutor(): Executor {
            return Executors.newSingleThreadExecutor()
        }

        fun provideViewModelFactory(context: Context): ViewModelFactory {
            val propertyDataRepository = providePropertyDataSource(context)
            val executor = provideExecutor()
            return ViewModelFactory(propertyDataRepository, executor)
        }
    }
}