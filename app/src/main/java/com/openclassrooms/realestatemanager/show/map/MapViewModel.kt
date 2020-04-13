package com.openclassrooms.realestatemanager.show.map

import android.view.View
import androidx.lifecycle.*
import androidx.sqlite.db.SimpleSQLiteQuery
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.show.geocode_model.Geocode
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.utils.setAddressToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(private val propertyDataRepository: PropertyDataRepository, private val addressDataRepository: AddressDataRepository, private val geocodeRepository: GeocodeRepository): ViewModel() {

    val propertiesLiveData = MediatorLiveData<List<PropertyModelForMap>>()
    private val addressesMutableLiveData = MutableLiveData<MutableMap<String, Address?>>(HashMap<String, Address?>())
    var propertiesFromResearchLiveData: LiveData<List<Property>?>? = null
    private val allPropertiesLiveData = propertyDataRepository.getAllProperties()
    val resetBtnLiveData = MutableLiveData<Int>()

    init {
        propertiesLiveData.addSource(allPropertiesLiveData, Observer {
            getPropertiesModelForMap(it, addressesMutableLiveData.value!!)
        })
        propertiesLiveData.addSource(addressesMutableLiveData, Observer {
            getPropertiesModelForMap(allPropertiesLiveData.value, it)
        })

    }

    private fun getPropertiesModelForMap(properties: List<Property>?, addresses: Map<String, Address?>) {
        if (properties == null) {
            return
        }

        properties.forEach {
            val hasAddress = addresses.containsKey(it.id_property)
            if (!hasAddress) {
                getAddressForPropertyId(it.id_property)
            }
        }

        val propertyModelsForList = properties.map {
            PropertyModelForMap(
                    it.id_property,
                    addresses[it.id_property],
                    it.price.toString(),
                    it.type)
        }
        propertiesLiveData.value = propertyModelsForList
    }


    private fun getAddressForPropertyId(idProperty: String) {
        addressesMutableLiveData.value?.let {
            it[idProperty] = null
            addressesMutableLiveData.value = it
        }
        viewModelScope.launch(Dispatchers.IO) {
            val address = addressDataRepository.getAddressOfOneProperty(idProperty)
            withContext(Dispatchers.Main){
                addressesMutableLiveData.value?.let {
                    it[idProperty] = address
                    addressesMutableLiveData.value = it
                }
            }
        }
    }

    fun getLatLng(address: Address, countryCode: String, key: String): LiveData<Geocode> {
        val txt = setAddressToString(address)
        return geocodeRepository.getLatLng(txt, countryCode, key)
    }

    fun stringToSimpleSQLiteQuery(query: String): SimpleSQLiteQuery {
        return SimpleSQLiteQuery(query)
    }

    /**
     * Search list of properties in PropertyDatabase according to query
     * Change source of MediatorLiveData propertiesLiveData
     * @param query the search query
     */
    fun searchInDatabase(query: String){
        propertyDataRepository.searchInDatabase(stringToSimpleSQLiteQuery(query)).let { properties ->
            propertiesFromResearchLiveData = properties
            propertiesLiveData.removeSource(allPropertiesLiveData)
            propertiesLiveData.addSource(properties, Observer {
                getPropertiesModelForMap(it, addressesMutableLiveData.value!!)
            })
        }
        setButtonVisibility(true)

    }

    /**
     * Reset the research by restore source (allPropertiesLiveData) of MediatorLiveData
     */
    fun reset(){
        propertiesFromResearchLiveData?.let { properties ->
            propertiesLiveData.removeSource(properties)
            propertiesLiveData.addSource(allPropertiesLiveData, Observer {
                getPropertiesModelForMap(it, addressesMutableLiveData.value!!)
            })
        }
        setButtonVisibility(false)
    }

    fun setButtonVisibility(visibility: Boolean){
        if(visibility){
            resetBtnLiveData.value = View.VISIBLE
        }else{
            resetBtnLiveData.value = View.GONE
        }
    }
}