package com.openclassrooms.realestatemanager.update_database

import android.util.Log
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.property.data.PropertyHelper

class FirestoreDataRepository {

    fun getAllPropertyFromFirestore(): ArrayList<Property> {
        val list = ArrayList<Property>()
        val propertyHelper = PropertyHelper()
        propertyHelper.getAllProperties().addOnCompleteListener{ task ->
            for (document in task.result!!){
                val place = document.toObject<Property>(Property::class.java)
                Log.d("Place from firebase", document.data.toString())
                list.add(place)
            }
        }
        return list
    }
}