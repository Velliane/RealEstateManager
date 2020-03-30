package com.openclassrooms.realestatemanager.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.collections.ArrayList

class SearchViewModel(private val context: Context): ViewModel() {

    fun constructQueryResearch(priceMinValue: Int, priceMaxValue: Int, types: List<String>, locations: List<String>, roomsMinValue: Int, roomsMaxValue: Int, bedroomsMinValue: Int, bedroomsMaxValue: Int): String {

        var query = String()
        query += "SELECT * FROM Property"
        var contains = false

        if (locations.isNotEmpty()){
            query += " INNER JOIN Address ON Property.id_property = Address.idProperty"
        }

        //-- Types --//
        if(types.isNotEmpty()){
            query += ifContains(contains)
            if(types.size == 1) {
                val type = types[0]
                query += " type LIKE '$type'"
            }else{
                query += " type IN ("
                query += constructQueryFromList(types)
            }
            contains = true
        }

        query += ifContains(contains)
        query += " price >= '$priceMinValue' AND price <= '$priceMaxValue'"
        query += " AND rooms_nbr >= '$roomsMinValue' AND rooms_nbr <= '$roomsMaxValue'"
        query += " AND bed_nbr >= '$bedroomsMinValue' AND bed_nbr <= '$bedroomsMaxValue'"
        contains = true

        if(locations.isNotEmpty()){
            query += ifContains(contains)
            query += constructLocationQuery(locations, "street")
            query += "OR"
            query += constructLocationQuery(locations, "city")
            query += "OR"
            query += constructLocationQuery(locations, "country")
        }
        Log.d("QUERY", query)
        return query
    }

    fun ifContains(contains: Boolean): String {
        return if(contains) {
            " AND"
        }else {
            " WHERE"
        }
    }

    private fun constructQueryFromList(list: List<String>): String{
        var query = String()
        list.forEachIndexed{ index, element ->
            query += "'$element'"
            if(index < list.size-1){
                query += ","
            }
        }
        query += ")"
        return query
    }

    private fun constructLocationQuery(list: List<String>, field: String): String {
        var query = String()
        list.forEachIndexed{ index, element ->
            val location = element.toUpperCase(Locale.ROOT)
            query += " UPPER(Address.$field) LIKE ('%$location%') "
            if(index < list.size-1){
                query += "OR"
            }
        }
        return query
    }


    fun getTypesList(): List<String> {
        val list = ArrayList<String>()
        for (type in TypeEnum.values()) {
            list.add(context.getString(type.res))
        }
        return list
    }

    fun getNearbyList(): List<NearbyEnum> {
        val list = ArrayList<NearbyEnum>()
        for(item in NearbyEnum.values()){
            list.add(item)
        }
        return list
    }

}