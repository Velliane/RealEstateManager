package com.openclassrooms.realestatemanager.property.model.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.openclassrooms.realestatemanager.show.geocode_model.AddressComponent

class Result {
    @SerializedName("address_components")
    @Expose
    var addressComponents: List<AddressComponent>? = null
    @SerializedName("formatted_address")
    @Expose
    var formattedAddress: String? = null
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
    @SerializedName("plus_code")
    @Expose
    var plusCode: PlusCode? = null
    @SerializedName("types")
    @Expose
    var types: List<String>? = null

}