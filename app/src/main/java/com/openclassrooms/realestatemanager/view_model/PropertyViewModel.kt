package com.openclassrooms.realestatemanager.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.database.repositories.AddressDataRepository
import com.openclassrooms.realestatemanager.database.repositories.GeocodeRepository
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.database.repositories.PropertyDataRepository
import com.openclassrooms.realestatemanager.model.Address
import com.openclassrooms.realestatemanager.model.geocode.Geocode
import com.openclassrooms.realestatemanager.utils.setAddressToString
import java.util.concurrent.Executor

class PropertyViewModel(private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val executor: Executor) : ViewModel() {

    fun getAllProperty(): LiveData<List<Property>> {
        return propertyDataRepository.getAllProperties()
    }

    fun addProperty(property: Property) {
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    fun updateProperty(property: Property) {
        executor.execute { propertyDataRepository.updateProperty(property) }
    }

    fun getPropertyFromId(id_property: String): LiveData<Property> {
        return propertyDataRepository.getPropertyFromId(id_property)
    }

    fun addAddress(address: Address) {
        executor.execute { addressDataRepository.addAddress(address) }
    }

    fun getAddressOfOnePorperty(id_property: String): LiveData<Address> {
        return addressDataRepository.getAddressOfOneProperty(id_property)
    }

    fun getAllAddress(): LiveData<List<Address>> {
        return addressDataRepository.getAllAddress()
    }

    fun updateAddress(address: Address) {
        executor.execute { addressDataRepository.updateAddress(address) }
    }

    fun getLatLng(address: Address, countryCode: String, key: String): LiveData<Geocode> {
        val txt = setAddressToString(address)
        return geocodeRepository.getLatLng(txt, countryCode, key)
    }
}