package com.openclassrooms.realestatemanager.model.geocode

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geocode {
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

}