package com.openclassrooms.realestatemanager.show

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.property.model.geocode.Geocode
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.utils.setAddressToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val context: Context, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository, private val firestoreDataRepository: FirestoreDataRepository, private val userDataRepository: UserDataRepository) : ViewModel() {

    private val propertiesLiveData = MutableLiveData<List<Property>>()
    private val addressLiveData = MutableLiveData<Address>()


    init {
        getAllProperty()
    }

    fun getUserById(userId: String): LiveData<User>{
        return userDataRepository.getUserById(userId)
    }

    //-- PROPERTIES --//
    private fun getAllProperty(): LiveData<List<Property>>{
        //viewModelScope.launch(Dispatchers.IO) {
            return propertyDataRepository.getAllProperties()
           // withContext(Dispatchers.Main){
            //    propertiesLiveData.value = list
           // }
       // }
    }


    fun getAddressOfOneProperty(id_property: String){
        viewModelScope.launch(Dispatchers.IO) {
            val address = addressDataRepository.getAddressOfOneProperty(id_property)
            withContext(Dispatchers.Main){
                addressLiveData.value = address
            }
        }
    }

    fun getLatLng(address: Address, countryCode: String, key: String): LiveData<Geocode> {
        val txt = setAddressToString(address)
        return geocodeRepository.getLatLng(txt, countryCode, key)
    }


    //-- UPDATE DATABASE --//
    fun updateDatabase(){
        val list = propertiesLiveData.value
        viewModelScope.launch(Dispatchers.IO) {
            firestoreDataRepository.updateDatabase(list, userDataRepository.getAllUsers())
        }
    }


}