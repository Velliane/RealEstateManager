package com.openclassrooms.realestatemanager.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel

class SearchViewModel(private val context: Context): ViewModel() {

    fun constructQueryResearch(priceMinValue: Int, priceMaxValue: Int, types: List<String>, roomsMinValue: Int, roomsMaxValue: Int, bedroomsMinValue: Int, bedroomsMaxValue: Int): String {

        var query = String()
        query += "SELECT * FROM Property"
        var contains = false

        //-- Types --//
        if(types.isNotEmpty()){
            query += ifContains(contains)
            if(types.size == 1) {
                val type = types[0]
                query += " type LIKE '$type'"
            }else{
                query += " type IN ("
                types.forEachIndexed{ index, element ->
                      query += "'$element'"
                    if (index < types.size-1){
                        query += ","
                    }
                }
                query += ")"
            }
            contains = true
        }

        query += ifContains(contains)
        query += " price >= '$priceMinValue' AND price <= '$priceMaxValue'"
        query += " AND rooms_nbr >= '$roomsMinValue' AND rooms_nbr <= '$roomsMaxValue'"
        query += " AND bed_nbr >= '$bedroomsMinValue' AND bed_nbr <= '$bedroomsMaxValue'"

        return query
    }

    fun ifContains(contains: Boolean): String {
        return if(contains) {
            " AND"
        }else {
            " WHERE"
        }
    }

    fun getTypesList(): List<String> {
        val list = ArrayList<String>()
        for (type in TypeEnum.values()) {
            list.add(context.getString(type.res))
        }
        return list
    }

}