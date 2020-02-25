package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.data.database.PropertyDao

class PropertyDataRepository(private val propertyDao: PropertyDao) {

    fun getAllProperties(): LiveData<List<Property>>{
        return propertyDao.getAllProperties()
    }

    fun addProperty(property: Property): Long{
        return propertyDao.addProperty(property)
    }

    fun getPropertyFromId(id_property: String): LiveData<Property>{
        return propertyDao.getPropertyFromId(id_property)
    }

    fun searchInDatabase(query: SupportSQLiteQuery): LiveData<List<Property>>{
        return propertyDao.searchInDatabase(query)
    }
}