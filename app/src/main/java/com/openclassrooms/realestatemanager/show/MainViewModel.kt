package com.openclassrooms.realestatemanager.show

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.LoginActivity
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val authUI: AuthUI, private val context: Context, private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val firestoreDataRepository: FirestoreDataRepository, private val userDataRepository: UserDataRepository) : ViewModel() {

    val progressUploadLiveData = MutableLiveData<Int>()
    val propertiesLiveData = MutableLiveData<List<Property>>()
    val addressLiveData = MutableLiveData<Address>()
    val userLiveData = MutableLiveData<User>()

    init {
       getAllProperty()
    }

    suspend fun getUserById(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val user = userDataRepository.getUserById(userId)
            withContext(Dispatchers.Main) {
                userLiveData.value = user
            }
        }
    }

    //-- PROPERTIES --//
    fun getAllProperty(): LiveData<List<Property>>{
        return propertyDataRepository.getAllProperties()
    }

    fun getAddressOfOneProperty(id_property: String){
        viewModelScope.launch(Dispatchers.IO) {
            val address = addressDataRepository.getAddressOfOneProperty(id_property)
            withContext(Dispatchers.Main){
                addressLiveData.value = address
            }
        }
    }

    //-- UPDATE DATABASE --//
    fun updateDatabase(){
        val list = propertiesLiveData.value
        progressUploadLiveData.value = 0
        viewModelScope.launch(Dispatchers.IO) {
            firestoreDataRepository.updateDatabase(list, userDataRepository.getAllUsers())
            withContext(Dispatchers.Main){
                progressUploadLiveData.value = 100
            }
        }
    }


    //-- LOG OUT --//
    fun logOut(){
        authUI.signOut(context).addOnCompleteListener {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    /**
     * Return LiveData<User> get from Firebase or Room according of Internet's connexion
     * @param displayName The name got from Firebase
     * @param photoUrl the url of the photo got from Firebase
     * @param email the email got from Firebase
     * @param id the user's id
     * @return LiveData<User>
     */
    fun updateHeader(displayName: String, photoUrl: String, email: String, id: String?): LiveData<User> {
        val user = User()
        if (Utils.isInternetAvailable(context)) {
            //-- If connected to internet, get user's information from Firebase --//
            if (photoUrl != "") {
                user.photo = photoUrl
            }
            user.name = displayName
            user.email = email
            userLiveData.value = user
        } else {
            //-- If not connected, get users' information from Room --//
            viewModelScope.launch(Dispatchers.IO) {
                getUserById(id!!)
            }
        }
        return userLiveData
    }

}