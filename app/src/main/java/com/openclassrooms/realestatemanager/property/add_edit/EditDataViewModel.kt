package com.openclassrooms.realestatemanager.property.add_edit

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.property.Address
import java.util.concurrent.Executor

class EditDataViewModel(private val context: Context, private val photoDataRepository: PhotoDataRepository, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val executor: Executor) : ViewModel() {

    fun addProperty(property: Property) {
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    fun save(property: Property, address: Address){
        executor.execute {
            propertyDataRepository.addProperty(property)
            addressDataRepository.addAddress(address)
        }
    }

    //-- PHOTOS --//
    fun savePhotos(bitmap: Bitmap, id_property: String, description: String, uri: Uri){
        saveImageToExternalStorage(bitmap, id_property, description)
        saveImageToFirebase(uri, id_property, description)
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap, id_property: String, description: String) {
        photoDataRepository.saveImageToExternalStorage(bitmap, id_property, description, context)
    }

    private fun saveImageToFirebase(uri: Uri, id_property: String, description: String) {
        photoDataRepository.saveImageToFirebase(uri, id_property, description)
    }

}