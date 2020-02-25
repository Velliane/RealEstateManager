package com.openclassrooms.realestatemanager.property.add_edit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.*
import com.openclassrooms.realestatemanager.photos.Photo
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.property.Address
import java.util.*
import java.util.concurrent.Executor

class EditDataViewModel(private val context: Context, private val photoDataRepository: PhotoDataRepository, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val executor: Executor) : ViewModel() {

    /** Helpers for Firestore Data */
    val propertyHelper = PropertyHelper()
    val addressHelper = AddressHelper()
    val linkHelper = LinkHelper()

    /**
     * Save data in Room and Firestore
     */
    fun save(propertyId: String, newProperty: Property, number: Int, street: String, zip_code: String, city: String, country: String, imageList: List<Photo>?){
        var property = getPropertyFromId(propertyId).value
        if (property == null){
            addProperty(newProperty)
            val addressId = UUID.randomUUID().toString()
            val newAddress = Address(addressId, number, street, zip_code, city, country, propertyId)
            addAddress(newAddress)
            saveInFirestore(newProperty.id_property, newProperty, addressId, newAddress)
            val map = HashMap<String, List<String>>()
            map["property_id"] = listOf(propertyId)
            linkHelper.addLink(addressId, map)
        }else{
            property = newProperty
            val oldAddress = getAddressOfOneProperty(property.id_property).value!!
            val id = oldAddress.id_address
            val newAddress = Address(id, number, street, zip_code, city, country, propertyId)
            addAddress(newAddress)
            saveInFirestore(propertyId, property, id, newAddress)
        }

        //savePhotos(bitmap, newProperty.id_property, description, uri)
    }

    private fun saveInFirestore(propertyId: String, property: Property, addressId: String, address: Address){
        propertyHelper.createProperty(propertyId, property)
        addressHelper.createAddress(addressId, address)
    }

    fun addProperty(property: Property) {
        executor.execute { propertyDataRepository.addProperty(property) }
    }
    //-- ADDRESS --//
    fun addAddress(address: Address){
        executor.execute { addressDataRepository.addAddress(address) }
    }
    fun getPropertyFromId(id_property: String): LiveData<Property> {
        return propertyDataRepository.getPropertyFromId(id_property)
    }

    fun getAddressOfOneProperty(id_property: String): LiveData<Address> {
        return addressDataRepository.getAddressOfOneProperty(id_property)
    }


    //-- PHOTOS --//
    fun savePhotos(bitmap: Bitmap, id_property: String, description: String, uri: Uri){
        photoDataRepository.saveImageToExternalStorage(bitmap, id_property, description, context)
        photoDataRepository.saveImageToFirebase(uri, id_property, description)
    }

}