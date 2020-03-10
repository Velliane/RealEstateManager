package com.openclassrooms.realestatemanager.show.map

import com.google.android.gms.maps.model.LatLng


data class PropertyModelForMap (

        val propertyId: String,
        val location: LatLng,
        val price: String
)