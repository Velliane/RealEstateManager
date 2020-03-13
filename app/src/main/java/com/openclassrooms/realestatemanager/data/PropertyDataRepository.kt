package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.database.PropertyDao

class PropertyDataRepository(private val propertyDao: PropertyDao) {

    fun getAllProperties(): LiveData<List<Property>>{
        return propertyDao.getAllProperties()
    }

    fun addProperty(property: Property): Long{
        return propertyDao.addProperty(property)
    }

    suspend fun getPropertyFromId(id_property: String): Property {
        return propertyDao.getPropertyFromId(id_property)
    }

   suspend fun searchInDatabase(query: SupportSQLiteQuery): List<Property> {
       val list = propertyDao.searchInDatabase(query)
        return list
    }

    fun updatePropertyType(type: String, id_property: String): Int {
        return propertyDao.updatePropertyType(type, id_property)
    }


}