package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.GooglePlacesAPI
import com.openclassrooms.realestatemanager.utils.MockGooglePlacesAPI
import okhttp3.OkHttpClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.io.IOException

class RetrofitTest {

    private lateinit var mockRetrofit: MockRetrofit

    @Before
    fun init(){
        val retrofit = Retrofit.Builder().baseUrl("http://test.com")
                .client(OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val networkBehavior = NetworkBehavior.create()

        mockRetrofit = MockRetrofit.Builder(retrofit).networkBehavior(networkBehavior).build()
    }

    @Test
    @Throws(IOException::class)
    fun getLatLng(){
        val delegate: BehaviorDelegate<GooglePlacesAPI> = mockRetrofit.create(GooglePlacesAPI::class.java)
        val mockGooglePlacesAPI = MockGooglePlacesAPI(delegate)

        val call = mockGooglePlacesAPI.getLatLng("24 allée des Mûriers 69450 St-Cyr-Mont-d'Or FRANCE", "FR", "123456789")
        val response = call.execute()

        val geocode = response.body()
        val list = geocode!!.results


        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(52.56478524, list!![0].geometry!!.location!!.lat)
        Assert.assertEquals(6.54787874, list[0].geometry!!.location!!.lng)
    }
}