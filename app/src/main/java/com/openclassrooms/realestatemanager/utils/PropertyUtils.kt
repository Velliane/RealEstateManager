package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property


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

fun compareByDate(property1: Property, property2: Property): Property {
    val date1 = parseStringDateToLocalDateTime(property1.date)
    val date2 = parseStringDateToLocalDateTime(property2.date)

    return if(date1.isBefore(date2)){
        property2
    }else{
        property1
    }

}

