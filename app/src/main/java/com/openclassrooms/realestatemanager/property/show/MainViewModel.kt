package com.openclassrooms.realestatemanager.property.show

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.photos.PhotoDataRepository
import com.openclassrooms.realestatemanager.property.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.property.model.geocode.Geocode
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.utils.compareByDate
import com.openclassrooms.realestatemanager.utils.setAddressToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class MainViewModel(private val context: Context, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val firestoreDataRepository: FirestoreDataRepository, private val photoDataRepository: PhotoDataRepository, private val executor: Executor) : ViewModel() {

    val propertiesLiveData = MutableLiveData<List<Property>>()
    val addressLiveData = MutableLiveData<Address>()

    //-- PROPERTIES --//
    fun getAllProperty(): LiveData<List<Property>>{
//        viewModelScope.launch(Dispatchers.IO) {
//            val list = propertyDataRepository.getAllProperties()
//            withContext(Dispatchers.Main){
//                propertiesLiveData.value = list
//            }
//        }
        return propertyDataRepository.getAllProperties()
    }

    fun addProperty(property: Property) {
        executor.execute { propertyDataRepository.addProperty(property) }
    }

    fun getPropertyFromId(id_property: String): LiveData<Property> {
        return propertyDataRepository.getPropertyFromId(id_property)
    }

    fun searchInDatabase(query: SupportSQLiteQuery): LiveData<List<Property>> {
        return propertyDataRepository.searchInDatabase(query)
    }

    //-- ADDRESS --//
    fun addAddress(address: Address){
        executor.execute { addressDataRepository.addAddress(address) }
    }

    fun getAddressOfOneProperty(id_property: String): LiveData<Address> {
        return addressDataRepository.getAddressOfOneProperty(id_property)
    }

    fun getAllAddress(): LiveData<List<Address>> {
        return addressDataRepository.getAllAddress()
    }

    fun getLatLng(address: Address, countryCode: String, key: String): LiveData<Geocode> {
        val txt = setAddressToString(address)
        return geocodeRepository.getLatLng(txt, countryCode, key)
    }

    //-- UPDATE DATABASE --//
    fun updateDatabase(){
        val list = getAllProperty().value
        viewModelScope.launch(Dispatchers.IO) {
            firestoreDataRepository.updateDatabase(list)
        }
    }

//    private suspend fun getAllPropertyFromFirestore(): List<Property> {
//        return firestoreDataRepository.getAllPropertyFromFirestore()
//    }
//
//    private suspend fun getAddressFromFirestore(id_property: String): Address{
//           return  firestoreDataRepository.getAddressFromFirestore(id_property)
//    }

//    fun updateDatabase(mainActivity: MainActivity) {
//        viewModelScope.launch {
//            val listFromFirestore = getAllPropertyFromFirestore()
//            getAllProperty().observe(mainActivity, Observer<List<Property>> {
//                if (it.isEmpty()) {
//                    for (propertyFromFirestore in listFromFirestore) {
//                        addProperty(propertyFromFirestore)
//                        viewModelScope.launch(Dispatchers.IO) {
//                            saveAddress(propertyFromFirestore.id_property)
//                        }
//                    }
//                } else {
//                    for (propertyFromFirestore in listFromFirestore) {
//                        var foundId = false
//                        for (property in it) {
//                            if (propertyFromFirestore.id_property == property.id_property) {
//                                if (propertyFromFirestore.date != property.date) {
//                                    val updatedProperty = compareByDate(property, propertyFromFirestore)
//                                    addProperty(updatedProperty)
//                                    viewModelScope.launch {
//                                        saveAddress(updatedProperty.id_property)
//                                    }
//                                }
//                                foundId = true
//                                break
//                            }
//                        }
//                        if (!foundId) {
//                            addProperty(propertyFromFirestore)
//                            viewModelScope.launch {
//                                saveAddress(propertyFromFirestore.id_property)
//                            }
//                        }
//                    }
//                }
//            })
//        }
//    }

//    private suspend fun saveAddress(id_property: String) {
//        val address = getAddressFromFirestore(id_property)
//        addAddress(address)
//    }

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