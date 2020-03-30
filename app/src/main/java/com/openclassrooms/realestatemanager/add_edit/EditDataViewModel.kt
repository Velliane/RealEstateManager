package com.openclassrooms.realestatemanager.add_edit

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.data.*
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.search.NearbyEnum
import com.openclassrooms.realestatemanager.search.TypeEnum
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class EditDataViewModel(private val context: Context, private val photoDataRepository: PhotoDataRepository, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val executor: Executor, private val userDataRepository: UserDataRepository) : ViewModel() {

    /** Helpers for Firestore Data */
    private val propertyHelper = PropertyHelper()
    private val addressHelper = AddressHelper()
    private val linkHelper = LinkHelper()

    var propertyLiveData = MutableLiveData<Property>()
    var addressLiveData = MutableLiveData<Address>()
    val userListLiveData = MutableLiveData<List<User>>()

    /**
     * Save data in Room and Firestore
     */
    fun save(propertyId: String, newProperty: Property, number: Int, street: String,
             zip_code: String, city: String, country: String, imageList: List<Photo>?,
             name: String, container: View) {

        getPropertyFromId(propertyId)
        var property: Property? = propertyLiveData.value
        //-- If property doesn't exist, create one and save it --//
        if (property == null) {
            newProperty.id_property = UUID.randomUUID().toString()
            addProperty(newProperty)
            updatePropertyType(context.getString(TypeEnum.valueOf(newProperty.type).res), newProperty.id_property)
            saveAddress(UUID.randomUUID().toString(), number, street, zip_code, city, country, newProperty.id_property, newProperty)
            savePhotos(imageList, newProperty.id_property)
        } else {
            //-- If property already exist, update it --//
            property = newProperty
            addProperty(property)
            updatePropertyType(context.getString(TypeEnum.valueOf(property.type).res), property.id_property)
            getAddressOfOneProperty(property.id_property)
            val oldAddress = addressLiveData.value!!
            val id = oldAddress.id_address
            saveAddress(id, number, street, zip_code, city, country, propertyId, property)
            savePhotos(imageList, property.id_property)
        }
        sendNotification(name)
        Snackbar.make(container, "Save complete", Snackbar.LENGTH_SHORT).show()
    }

    private fun sendNotification(name: String){
        val data = Data.Builder().putString(Constants.DATA_USER_NAME, name).build()
        NotificationWorker.configureNotification(data)
    }

    fun getNearby(listNearby: List<String>): String{
        var nearby = ""
        listNearby.forEachIndexed{ index, element ->
            nearby += element.toUpperCase(Locale.ROOT)
            if (index < listNearby.size-1){
                nearby += ","
            }
        }
        return nearby
    }


    //-- GET LIST FOR ADAPTERS --//
    fun getTypesList(): List<TypeEnum> {
        val list = ArrayList<TypeEnum>()
        for (type in TypeEnum.values()) {
            list.add(type)
        }
        return list
    }

    fun getNearbyList(): List<NearbyEnum> {
        val list = ArrayList<NearbyEnum>()
        for(item in NearbyEnum.values()){
            list.add(item)
        }
        return list
    }


    //-- ADD DATA IN FIRESTORE --//
    private fun saveInFirestore(propertyId: String, property: Property, addressId: String, address: Address) {
        propertyHelper.createProperty(propertyId, property)
        addressHelper.createAddress(addressId, address)
    }

    private fun saveAddress(id_address: String, number: Int, street: String, zip_code: String, city: String, country: String, id_property: String, property: Property){
        val address = Address(id_address, number, street, zip_code, city, country, id_property)
        addAddress(address)
        saveInFirestore(id_property, property, id_address, address)
        val map = HashMap<String, List<String>>()
        map["property_id"] = listOf(id_property)
        linkHelper.addLink(id_address, map)
    }


    //-- ADD DATA IN ROOM --//
    fun addProperty(property: Property) {
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    private fun updatePropertyType(type: String, id_property: String) {
        executor.execute{ propertyDataRepository.updatePropertyType(type, id_property)}
    }

    private fun addAddress(address: Address) {
        executor.execute { addressDataRepository.addAddress(address) }
    }


    //-- GET DATA --//
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

    fun getAllUser(){
        viewModelScope.launch {
            val list = userDataRepository.getAllUsers()
            withContext(Dispatchers.Main){
                userListLiveData.value = list
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