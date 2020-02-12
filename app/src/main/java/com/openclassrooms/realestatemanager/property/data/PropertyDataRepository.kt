package com.openclassrooms.realestatemanager.property.data

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.property.Property

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

}