package com.openclassrooms.realestatemanager.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository
import java.util.concurrent.Executor

class PropertyViewModel(private val propertyDataRepository: PropertyDataRepository, private val executor: Executor) : ViewModel() {

    fun getAllProperty(): LiveData<List<Property>> {
        return propertyDataRepository.getAllProperties()
    }

    fun addProperty(property: Property){
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    fun updateProperty(property: Property){
        executor.execute { propertyDataRepository.updateProperty(property) }
    }

    fun getPropertyFromId(id_property: Int): LiveData<Property>{
        return propertyDataRepository.getPropertyFromId(id_property)
    }
}