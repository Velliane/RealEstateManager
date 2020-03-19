package com.openclassrooms.realestatemanager.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel

class SearchViewModel(private val context: Context): ViewModel() {

    fun searchDatabase(spinnerPriceSelected: String, types: List<String>, roomsMinValue: Int, roomsMaxValue: Int): String{

        val priceRange = ArrayList<Int>()
        if(PriceRangeEnum.valueOf(spinnerPriceSelected) != PriceRangeEnum.ANY){
            val priceEnum: PriceRangeEnum = PriceRangeEnum.valueOf(spinnerPriceSelected)
            priceRange.add(priceEnum.minValue)
            priceRange.add(priceEnum.maxValue)
        }

        return constructQueryResearch(priceRange, types, roomsMinValue, roomsMaxValue)
    }

    private fun constructQueryResearch(priceRange: ArrayList<Int>, types: List<String>, roomsMinValue: Int, roomsMaxValue: Int): String {

        var query = String()
        query += "SELECT * FROM Property"
        var contains = false

        if(priceRange.isNotEmpty()){
            val min = priceRange[0]
            val max = priceRange[1]
            query += " WHERE price BETWEEN '$min' AND '$max'"
            Log.d("QUERY", query)
            contains = true
        }

        if(types.isNotEmpty()){
            query += if (contains){
                " AND"
            }else{
                " WHERE"
            }
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
            Log.d("QUERY", query)
            contains = true
        }

        query += if(contains){
            " AND"
        }else{
            " WHERE"
        }
        query += " rooms_nbr >= '$roomsMinValue' AND rooms_nbr <= '$roomsMaxValue'"
        Log.d("QUERY", query)

        return query
    }


    fun getPriceRangeList(): List<PriceRangeEnum>{
        val list = ArrayList<PriceRangeEnum>()
        for (price in PriceRangeEnum.values()) {
            list.add(price)
        }
        return list
    }

    fun getTypesList(): List<String> {
        val list = ArrayList<String>()
        for (type in TypeEnum.values()) {
            list.add(context.getString(type.res))
        }
        return list
    }

}