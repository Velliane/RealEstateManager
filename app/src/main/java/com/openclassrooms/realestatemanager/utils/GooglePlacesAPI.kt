package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.show.geocode_model.Geocode
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPI {

    @GET("maps/api/geocode/json?")
    fun getLatLng(@Query("address")address: String, @Query("components") countryCode: String, @Query("key") key: String): Call<Geocode>

    companion object{
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}