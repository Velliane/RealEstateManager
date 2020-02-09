package com.openclassrooms.realestatemanager.property.data.geocode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.property.model.geocode.Geocode
import com.openclassrooms.realestatemanager.utils.GooglePlacesAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GeocodeRepository {

    private val retrofit = GooglePlacesAPI.retrofit.create(GooglePlacesAPI::class.java)

    fun getLatLng(address: String, countryCode: String, key: String): LiveData<Geocode> {

        val data = MutableLiveData<Geocode>()
        retrofit.getLatLng(address, countryCode, key).enqueue(object : Callback<Geocode> {
            override fun onFailure(call: Call<Geocode>, t: Throwable) {
                data.value = null
            }

            override fun onResponse(call: Call<Geocode>, response: Response<Geocode>) {
                if (response.isSuccessful){
                    data.value = response.body()
                }
            }
        })
        return data
    }
}