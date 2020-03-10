package com.openclassrooms.realestatemanager.show.list

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.utils.getDefaultPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for ListViewFragment
 * Expose a MediatorLiveDate with two sources:
 *  - list of all properties get from Firestore
 *  - list of all Addresses according of properties's id
 * Merge the two lists
 */
class ListViewModel(propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val photoDataRepository: PhotoDataRepository) : ViewModel() {

    val propertiesLiveData = MediatorLiveData<List<PropertyModelForList>>()
    private val addressesMutableLiveData = MutableLiveData<MutableMap<String, Address?>>(HashMap<String, Address?>())

    init {
        val allPropertiesLiveData = propertyDataRepository.getAllProperties()
        propertiesLiveData.addSource(allPropertiesLiveData, Observer {
            combinePropertiesPhotosAndAddresses(it, addressesMutableLiveData.value!!)
        })
        propertiesLiveData.addSource(addressesMutableLiveData, Observer {
            combinePropertiesPhotosAndAddresses(allPropertiesLiveData.value, it)
        })
    }

    private fun combinePropertiesPhotosAndAddresses(properties: List<Property>?, addresses: Map<String, Address?>) {
        if (properties == null) {
            return
        }
        properties.forEach {
            val hasAddress = addresses.containsKey(it.id_property)
            if (!hasAddress) {
                getAddressForPropertyId(it.id_property)
            }
        }

        val propertyModelsForList = properties.sortedBy { it.date }.map {
            PropertyModelForList(
                    it.id_property,
                    it.type,
                    it.price.toString(),
                    addresses[it.id_property]?.city,
                    getPhotoForPropertyId(it.id_property))
        }
        propertiesLiveData.value = propertyModelsForList
    }

    private fun getPhotoForPropertyId(idProperty: String): Photo {
        var photo = getDefaultPhoto()
        val listPhoto = photoDataRepository.getListOfPhotos(idProperty) as ArrayList<Photo>

        if(listPhoto.isNotEmpty()){
            photo = listPhoto[0]
        }
        return photo
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