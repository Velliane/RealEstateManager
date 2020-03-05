package com.openclassrooms.realestatemanager.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.openclassrooms.realestatemanager.data.PropertyHelper
import com.openclassrooms.realestatemanager.add_edit.Property
import retrofit2.mock.BehaviorDelegate

class MockPropertyHelper(private val delegate: BehaviorDelegate<PropertyHelper>): PropertyHelper(){

    override fun getPropertiesCollection(): CollectionReference {
        return super.getPropertiesCollection()
    }

    override fun createProperty(id_property: String, newProperty: Property): Task<Void> {
        return super.createProperty(id_property, newProperty)
    }

    override suspend fun getAllProperties(): List<DocumentSnapshot> {
        return super.getAllProperties()
    }

    override fun getProperty(id_property: String): Task<DocumentSnapshot> {
        return super.getProperty(id_property)
    }

    override suspend fun updatePhotos(id_property: String, list: List<String>): Task<Void> {
        return super.updatePhotos(id_property, list)
    }
}