package com.openclassrooms.realestatemanager.utils

import android.util.Log
import com.openclassrooms.realestatemanager.api.getAllProperties
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.Property
import java.lang.StringBuilder

fun setAddressToString(address: Address): String {
    val number = address.number
    val street = address.street
    val zipCode = address.zip_code
    val city = address.city
    val country = address.country
    val addressTxt = StringBuilder("")
    addressTxt.append(number)
    addressTxt.append(" $street")
    addressTxt.append("\n")
    addressTxt.append(zipCode)
    addressTxt.append(" $city")
    addressTxt.append("\n")
    addressTxt.append(country)
    return addressTxt.toString()
}

fun getLatLngLocation(address: Address): String{
    return "NC"
}

private fun getAllPropertyFromFirebase(): ArrayList<Property> {
    val list = ArrayList<Property>()
    getAllProperties().addOnCompleteListener{ task ->
        for (document in task.result!!){
            val place = document.toObject<Property>(Property::class.java)
            Log.d("Place from firebase", document.data.toString())
            list.add(place)
        }
    }
    return list
}

