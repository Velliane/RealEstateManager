package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.model.Address
import java.lang.StringBuilder

fun setAddressToString(address: Address): String {
    val number = address.number
    val street = address.street
    val zipCode = address.zip_code
    val city = address.city
    val country = address.country
    val addressTxt = StringBuilder("")
    addressTxt.append(number)
    addressTxt.append(street)
    addressTxt.append(zipCode)
    addressTxt.append(city)
    addressTxt.append("/n")
    addressTxt.append(country)
    return addressTxt.toString()
}

fun getLatLngLocation(address: Address): String{
    return "NC"
}