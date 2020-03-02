package com.openclassrooms.realestatemanager.property.add_edit

import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.*
import com.openclassrooms.realestatemanager.photos.Photo
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property
import java.util.*
import java.util.concurrent.Executor

class EditDataViewModel(private val context: Context, private val photoDataRepository: PhotoDataRepository, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val executor: Executor) : ViewModel() {

    /** Helpers for Firestore Data */
    private val propertyHelper = PropertyHelper()
    private val addressHelper = AddressHelper()
    private val linkHelper = LinkHelper()

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

        //if(checkPermission()){
            savePhotos(imageList, newProperty.id_property)
        //}
    }

    //-- Add Data in Firestore --//
    private fun saveInFirestore(propertyId: String, property: Property, addressId: String, address: Address){
        propertyHelper.createProperty(propertyId, property)
        addressHelper.createAddress(addressId, address)
    }

    //-- Add Data in Room --//
    private fun addProperty(property: Property) {
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    private fun addAddress(address: Address){
        executor.execute { addressDataRepository.addAddress(address) }
    }

    //-- Get Data --//
    fun getPropertyFromId(id_property: String): LiveData<Property> {
        return propertyDataRepository.getPropertyFromId(id_property)
    }

    fun getAddressOfOneProperty(id_property: String): LiveData<Address> {
        return addressDataRepository.getAddressOfOneProperty(id_property)
    }


    //-- PHOTOS --//
    private fun savePhotos(imageList: List<Photo>?, id_property: String){
        if (imageList != null) {
            for(image in imageList){
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, image.uri)

//                val path = image.uri!!.path
//                try {
//                    val cursor = context.contentResolver.query(image.uri!!, null, null, null)
//                }
                photoDataRepository.saveImageToExternalStorage(bitmap, id_property, image.description!!, context)
                photoDataRepository.saveImageToFirebase(image.uri!!, id_property, image.description!!)
            }
        }

    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

}