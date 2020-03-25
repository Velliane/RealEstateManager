package com.openclassrooms.realestatemanager.show.list

import com.openclassrooms.realestatemanager.add_edit.Photo

data class PropertyModelForList (

        val propertyId: String,
        val type: String,
        val price: String,
        val location: String?,
        val photo: Photo?,
        val isSelected: Boolean
)