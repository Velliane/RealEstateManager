package com.openclassrooms.realestatemanager.update_database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class FirestoreDataViewModelFactory(private val firestoreDataRepository: FirestoreDataRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FirestoreDataViewModel::class.java)){
            return FirestoreDataViewModel(firestoreDataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}