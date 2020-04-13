package com.openclassrooms.realestatemanager.show.geocode_model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Result {
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null

}