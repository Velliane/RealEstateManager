package com.openclassrooms.realestatemanager.update_database

import android.util.Log
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.property.data.PropertyHelper

class FirestoreDataRepository {

    suspend fun getAllPropertyFromFirestore(): ArrayList<Property> {
        val list = ArrayList<Property>()
        val propertyHelper = PropertyHelper()
        val listDocument = propertyHelper.getAllProperties()
        for(document in listDocument){
            val place = document.toObject<Property>(Property::class.java)
            Log.d("Place from firebase", document.data.toString())
            list.add(place!!)
        }
        return list
    }

    fun getAddressFromFirestore(id_property: String): Address {
//        val propertyHelper = PropertyHelper()
//        val query = propertyHelper.getAddress(id_property)
//        return query!!.documents[0].toObject(Address::class.java)!!
        return Address(id_property, 1, "Rue des lilas", "35500", "blablaville", "France", id_property)
    }



}