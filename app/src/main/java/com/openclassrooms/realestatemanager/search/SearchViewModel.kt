package com.openclassrooms.realestatemanager.search

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.sqlite.db.SimpleSQLiteQuery

class SearchViewModel(private val context: Context): ViewModel() {

    fun searchDatabase(spinnerPriceSelected: String, spinnerTypeSelected: String, roomsMinValue: Int, roomsMaxValue: Int): SimpleSQLiteQuery{

        val priceRange = ArrayList<Int>()
        if(PriceRangeEnum.valueOf(spinnerPriceSelected) != PriceRangeEnum.ANY){
            val priceEnum: PriceRangeEnum = PriceRangeEnum.valueOf(spinnerPriceSelected)
            priceRange.add(priceEnum.minValue)
            priceRange.add(priceEnum.maxValue)
        }

        var type = ""
        if(TypeEnum.valueOf(spinnerTypeSelected) != TypeEnum.ANY){
            val enum = TypeEnum.valueOf(spinnerTypeSelected)
            type = context.getString(enum.res)
        }

        return constructQueryResearch(priceRange, type, roomsMinValue, roomsMaxValue)
    }

    private fun constructQueryResearch(priceRange: ArrayList<Int>, type: String, roomsMinValue: Int, roomsMaxValue: Int): SimpleSQLiteQuery {

        var query = String()
        query += "SELECT * FROM Property"
        var contains = false

        if(priceRange.isNotEmpty()){
            val min = priceRange[0]
            val max = priceRange[1]
            query += " WHERE price >= :$min AND price <= :$max"
            Log.d("QUERY", query)
            contains = true
        }

        if(type != ""){
            if (contains){
                query += " AND"
            }
            query += " WHERE type == :$type"
            Log.d("QUERY", query)
            contains = true
        }

        if(contains){
            query += " AND"
        }
        query += " WHERE rooms_nbr >= :$roomsMinValue AND rooms_nbr <= :$roomsMaxValue"
        Log.d("QUERY", query)

        return SimpleSQLiteQuery(query)
    }

    fun getPriceRangeList(): List<PriceRangeEnum>{
        val list = ArrayList<PriceRangeEnum>()
        for (price in PriceRangeEnum.values()) {
            list.add(price)
        }
        return list
    }

    fun getTypesList(): List<TypeEnum> {
        val list = ArrayList<TypeEnum>()
        for (type in TypeEnum.values()) {
            list.add(type)
        }
        return list
    }

}