package com.openclassrooms.realestatemanager.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.data.database.PropertyDao

class FakePropertyDataRepository(private val propertyDao: PropertyDao): PropertyDataRepository(propertyDao) {

    override fun getAllProperties(): LiveData<List<Property>> {
        val listLiveData = MutableLiveData<List<Property>>()
        val list = ArrayList<Property>()
        //-- Create Properties and add them to list --//
        val property1 = Property("001", "House", 250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25")
        val property2 = Property("002", "House", 185000, 75, 3, 1, 2, "Little house", true, "2020-03-09T12:20:25")
        list.add(property1)
        list.add(property2)
        //-- Set value of LiveData --//
        listLiveData.value = list
        return listLiveData
    }

    override fun addProperty(property: Property): Long {
        return super.addProperty(property)
    }

    override suspend fun getPropertyFromId(id_property: String): Property {
        var property = Property()
        if(id_property == "001"){
            property = Property("001", "House", 250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25")
        }else if(id_property == "002"){
            property = Property("002", "House", 185000, 75, 3, 1, 2, "Little house", true, "2020-03-09T12:20:25")
        }
        return property
    }

    override suspend fun searchInDatabase(query: SupportSQLiteQuery): List<Property> {
        return super.searchInDatabase(query)
    }

    override fun updatePropertyType(type: String, id_property: String): Int {
        return super.updatePropertyType(type, id_property)
    }
}