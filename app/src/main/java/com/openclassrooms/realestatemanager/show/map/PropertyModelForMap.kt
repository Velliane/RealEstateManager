package com.openclassrooms.realestatemanager.show.map

import com.openclassrooms.realestatemanager.add_edit.Address


data class PropertyModelForMap (

        val propertyId: String,
        val address: Address?,
        val price: String
)