package com.openclassrooms.realestatemanager.show.map

import android.content.Context
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.property.model.geocode.Geocode
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.utils.setAddressToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(private val context: Context, propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository): ViewModel() {

    val propertiesLiveData = MediatorLiveData<List<PropertyModelForMap>>()
    private val addressesMutableLiveData = MutableLiveData<MutableMap<String, Address?>>(HashMap<String, Address?>())

    init {
        val allPropertiesLiveData = propertyDataRepository.getAllProperties()
        propertiesLiveData.addSource(allPropertiesLiveData, Observer {
            getPropertiesModelForMap(it, addressesMutableLiveData.value!!)
        })
        propertiesLiveData.addSource(addressesMutableLiveData, Observer {
            getPropertiesModelForMap(allPropertiesLiveData.value, it)
        })

    }

    private fun getPropertiesModelForMap(properties: List<Property>?, addresses: Map<String, Address?>) {
        if (properties == null) {
            return
        }

        properties.forEach {
            val hasAddress = addresses.containsKey(it.id_property)
            if (!hasAddress) {
                getAddressForPropertyId(it.id_property)
            }
        }

        val propertyModelsForList = properties.map {
            PropertyModelForMap(
                    it.id_property,
                    addresses[it.id_property],
                    it.price.toString())
        }
        propertiesLiveData.value = propertyModelsForList
    }

    fun getLatLng(address: Address, countryCode: String, key: String): LiveData<Geocode> {
        val txt = setAddressToString(address)
        return geocodeRepository.getLatLng(txt, countryCode, key)
    }


    private fun getLatLngForAddress(address: Address, countryCode: String, key: String): LatLng {
        val txt = setAddressToString(address)
        val geocodeMutableLiveData = geocodeRepository.getLatLng(txt, countryCode, key)
        val geocode = geocodeMutableLiveData.value!!.results!![0]
        return LatLng(geocode.geometry!!.location!!.lat!!, geocode.geometry!!.location!!.lng!!)
    }

    private fun getAddressForPropertyId(idProperty: String) {
        addressesMutableLiveData.value?.let {
            it[idProperty] = null
            addressesMutableLiveData.value = it
        }
        viewModelScope.launch(Dispatchers.IO) {
            val address = addressDataRepository.getAddressOfOneProperty(idProperty)
            withContext(Dispatchers.Main){
                addressesMutableLiveData.value?.let {
                    it[idProperty] = address
                    addressesMutableLiveData.value = it
                }
            }
        }

    }

}