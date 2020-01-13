package com.openclassrooms.realestatemanager.repositories

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.model.Property

class PropertyDataRepository(private val propertyDao: PropertyDao) {

    fun getAllProperties(): LiveData<List<Property>>{
        return propertyDao.getAllProperties()
    }

    fun addProperty(property: Property): Long{
        return propertyDao.addProperty(property)
    }

    fun updateProperty(property: Property): Int{
        return propertyDao.updateProperty(property)
    }

    fun getPropertyFromId(id_property: Int): Int{
        return propertyDao.getPropertyFromId(id_property)
    }

}