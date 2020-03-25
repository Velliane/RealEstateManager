package com.openclassrooms.realestatemanager.show.detail

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.property.model.geocode.Geocode
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.utils.setAddressToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel(private val context: Context, private val propertyDataRepository: PropertyDataRepository, private val geocodeRepository: GeocodeRepository, private val addressDataRepository: AddressDataRepository, private val photoDataRepository: PhotoDataRepository, private val userDataRepository: UserDataRepository): ViewModel() {

    var propertyLiveData = MutableLiveData<Property>()
    val addressLiveData = MutableLiveData<Address>()
    val listPhotosLiveData = MutableLiveData<List<Photo>>()
    val agentLiveData = MutableLiveData<User>()

    /**
     * Get Property by his id from PropertyDatabase
     */
    fun getPropertyFromId(id_property: String){
        viewModelScope.launch(Dispatchers.IO) {
            val property = propertyDataRepository.getPropertyFromId(id_property)
            withContext(Dispatchers.Main){
                propertyLiveData.value = property
            }
        }
    }

    /**
     * Get Address by Property's id
     */
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

    fun getListOfPhotos(id_property: String){
        listPhotosLiveData.value = photoDataRepository.getListOfPhotos(id_property)
    }

    fun setAgent(id_agent: String){
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDataRepository.getUserById(id_agent)
            withContext(Dispatchers.Main){
                agentLiveData.value = user
            }
        }
    }

//    fun setAgent(id_agent: String, txtView: TextView){
//        val userLiveData = getAgentById(id_agent)
//        if(userLiveData.value != null){
//            txtView.text = context.getString(R.string.manage_by, userLiveData.value!!.name)
//        }
//    }
}