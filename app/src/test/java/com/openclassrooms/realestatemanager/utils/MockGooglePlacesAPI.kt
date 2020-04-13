package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.show.geocode_model.Geocode
import com.openclassrooms.realestatemanager.show.geocode_model.Geometry
import com.openclassrooms.realestatemanager.show.geocode_model.Location
import com.openclassrooms.realestatemanager.show.geocode_model.Result
import retrofit2.Call
import retrofit2.mock.BehaviorDelegate

class MockGooglePlacesAPI(private val delegate: BehaviorDelegate<GooglePlacesAPI>): GooglePlacesAPI {


    override fun getLatLng(address: String, countryCode: String, key: String): Call<Geocode> {
        val geocode = Geocode()

        val list = ArrayList<Result>()
        val result1 = createResult()
        list.add(result1)

        geocode.results = list
        return delegate.returningResponse(geocode).getLatLng("24 allée des Mûriers 69450 St-Cyr-Mont-d'Or FRANCE", "FR", "123456789")
    }

    private fun createResult(): Result {

        val result = Result()
        // Create location
        val geometry = Geometry()
        val location = Location()
        val latitude= 52.56478524
        val longitude = 6.54787874
        location.lat = latitude
        location.lng = longitude
        geometry.location = location
        result.geometry = geometry

        return result
    }


}