package com.openclassrooms.realestatemanager.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.realestatemanager.data.PropertyHelper
import retrofit2.mock.BehaviorDelegate

class MockPropertyHelper(private val delegate: BehaviorDelegate<PropertyHelper>): PropertyHelper(){

    override fun getPropertiesCollection(): CollectionReference {
        return super.getPropertiesCollection()
    }

    override fun createProperty(id_property: String, type: String, price: Int, surface: Int, rooms_nbr: Int, bath_nbr: Int, bed_nbr: Int, description: String, in_sale: Boolean, address: String, date: String): Task<Void> {
        return super.createProperty(id_property, type, price, surface, rooms_nbr, bath_nbr, bed_nbr, description, in_sale, address, date)
    }

    override fun getAllProperties(): Task<QuerySnapshot> {
        return super.getAllProperties()
    }

    override fun getProperty(id_property: Int): Task<DocumentSnapshot> {
        return super.getProperty(id_property)
    }
}