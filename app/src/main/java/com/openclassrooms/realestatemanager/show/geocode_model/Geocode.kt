package com.openclassrooms.realestatemanager.show.geocode_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geocode {
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null

}