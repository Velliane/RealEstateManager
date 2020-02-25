package com.openclassrooms.realestatemanager.utils

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property
import kotlin.collections.ArrayList


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

fun getLatLngLocation(address: Address): String {
    return "NC"
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

fun constructQueryResearch(priceRange: ArrayList<Int>): SimpleSQLiteQuery {

    var query = String()
    query += "SELECT * FROM Property"

    if(priceRange.isNotEmpty()){
        val min = priceRange[0]
        val max = priceRange[1]
        query += " WHERE price >= :$min AND price <= :$max"
        Log.d("QUERY", query)
    }

    return SimpleSQLiteQuery(query)
}




