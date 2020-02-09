package com.openclassrooms.realestatemanager.update_database

import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.property.Property

class FirestoreDataViewModel(private val firestoreDataRepository: FirestoreDataRepository): ViewModel() {

    fun getAllPropertyFromFirestore(): ArrayList<Property>{
        return firestoreDataRepository.getAllPropertyFromFirestore()
    }
}