package com.openclassrooms.realestatemanager.add_edit

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.*
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.search.TypeEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executor

class EditDataViewModel(private val context: Context, private val photoDataRepository: PhotoDataRepository, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val executor: Executor) : ViewModel() {

    /** Helpers for Firestore Data */
    private val propertyHelper = PropertyHelper()
    private val addressHelper = AddressHelper()
    private val linkHelper = LinkHelper()

    var propertyLiveData = MutableLiveData<Property>()
    var addressLiveData = MutableLiveData<Address>()
    var listPhotosLiveData = MutableLiveData<List<String>?>()

    /**
     * Save data in Room and Firestore
     */
    fun save(propertyId: String, newProperty: Property, number: Int, street: String, zip_code: String, city: String, country: String, imageList: List<Photo>?) {


        getPropertyFromId(propertyId)
        var property: Property? = propertyLiveData.value
        if (property == null) {
            addProperty(newProperty)
            newProperty.id_property = UUID.randomUUID().toString()
            val addressId = UUID.randomUUID().toString()
            val newAddress = Address(addressId, number, street, zip_code, city, country, newProperty.id_property)
            addAddress(newAddress)
            saveInFirestore(newProperty.id_property, newProperty, addressId, newAddress)
            val map = HashMap<String, List<String>>()
            map["property_id"] = listOf(newProperty.id_property)
            linkHelper.addLink(addressId, map)
            savePhotos(imageList, newProperty.id_property)

        } else {
            property = newProperty
            getAddressOfOneProperty(property.id_property)
            val oldAddress = addressLiveData.value!!
            val id = oldAddress.id_address
            val newAddress = Address(id, number, street, zip_code, city, country, propertyId)
            addAddress(newAddress)
            saveInFirestore(propertyId, property, id, newAddress)
            savePhotos(imageList, property.id_property)
        }

    }

    fun getTypesList(): List<TypeEnum> {
        val list = ArrayList<TypeEnum>()
        for (type in TypeEnum.values()) {
            if(type != TypeEnum.ANY){
                list.add(type)
            }
        }
        return list
    }

    //-- Add Data in Firestore --//
    private fun saveInFirestore(propertyId: String, property: Property, addressId: String, address: Address) {
        propertyHelper.createProperty(propertyId, property)
        addressHelper.createAddress(addressId, address)
    }

    //-- Add Data in Room --//
    private fun addProperty(property: Property) {
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    private fun addAddress(address: Address) {
        executor.execute { addressDataRepository.addAddress(address) }
    }

    //-- Get Data --//
    fun getPropertyFromId(id_property: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val property = propertyDataRepository.getPropertyFromId(id_property)
            withContext(Dispatchers.Main) {
                propertyLiveData.value = property
            }
        }
    }

    fun getAddressOfOneProperty(id_property: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val address = addressDataRepository.getAddressOfOneProperty(id_property)
            withContext(Dispatchers.Main) {
                addressLiveData.value = address
            }
        }
    }


    //-- PHOTOS --//
    private fun savePhotos(imageList: List<Photo>?, id_property: String) {
        if (imageList != null) {
            for (image in imageList) {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, image.uri)
                photoDataRepository.saveImageToExternalStorage(bitmap, id_property, image.description)
                photoDataRepository.saveImageToFirebase(image.uri, id_property, image.description)
            }
        }
    }

}