package com.openclassrooms.realestatemanager.utils

import android.content.Context
import android.net.Uri
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.add_edit.Property


fun setAddressToString(address: Address): String {
    val number = address.number
    val street = address.street
    val zipCode = address.zip_code
    val city = address.city
    val country = address.country
    val addressTxt = StringBuilder("")

    if(number != null) {
        addressTxt.append("$number ")
        if(street != null){
            addressTxt.append("$street")
        }
    }
    if(number == null && street != null){
        addressTxt.append("$street")
    }
    if(number != null || street != null){
        addressTxt.append("\n")
    }
    if(zipCode != null) {
        addressTxt.append("$zipCode ")
    }

    addressTxt.append(city)
    addressTxt.append("\n")
    addressTxt.append(country)
    return addressTxt.toString()
}

fun compareByDate(property1: Property, property2: Property): Property {
    val date1 = parseStringDateToLocalDateTime(property1.date)
    val date2 = parseStringDateToLocalDateTime(property2.date)

    return if (date1.isBefore(date2)) {
        property2
    } else {
        property1
    }
}

fun getDefaultPhoto(context: Context): Photo {
    val default = context.getString(R.string.default_photo_uri)
    return Photo(Uri.parse(default), "No image")
}




