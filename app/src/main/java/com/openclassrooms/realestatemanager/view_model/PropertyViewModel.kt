package com.openclassrooms.realestatemanager.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.database.repositories.AddressDataRepository
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.model.Address
import java.util.concurrent.Executor

class PropertyViewModel(private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val executor: Executor) : ViewModel() {

    fun getAllProperty(): LiveData<List<Property>> {
        return propertyDataRepository.getAllProperties()
    }

    fun addProperty(property: Property){
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    fun updateProperty(property: Property){
        executor.execute { propertyDataRepository.updateProperty(property) }
    }

    fun getPropertyFromId(id_property: String): LiveData<Property>{
        return propertyDataRepository.getPropertyFromId(id_property)
    }

    fun addAddress(address: Address){
        executor.execute { addressDataRepository.addAddress(address) }
    }
}