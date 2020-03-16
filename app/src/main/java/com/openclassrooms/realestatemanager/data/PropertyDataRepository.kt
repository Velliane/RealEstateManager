package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.database.PropertyDao

open class PropertyDataRepository(private val propertyDao: PropertyDao) {

   open fun getAllProperties(): LiveData<List<Property>>{
        return propertyDao.getAllProperties()
    }

    open fun addProperty(property: Property): Long{
        return propertyDao.addProperty(property)
    }

    open suspend fun getPropertyFromId(id_property: String): Property {
        return propertyDao.getPropertyFromId(id_property)
    }

   open fun searchInDatabase(query: SupportSQLiteQuery): LiveData<List<Property>> {
       return propertyDao.searchInDatabase(query)
    }

    open fun updatePropertyType(type: String, id_property: String): Int {
        return propertyDao.updatePropertyType(type, id_property)
    }


}