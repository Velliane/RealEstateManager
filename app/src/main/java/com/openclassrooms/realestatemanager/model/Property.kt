package com.openclassrooms.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity

data class Property(

        @PrimaryKey(autoGenerate = true)
        var id_property: Int,

        var type: String,
        var price: Int,
        var surface: Float
)