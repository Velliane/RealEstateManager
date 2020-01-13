package com.openclassrooms.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.w3c.dom.Text

@Entity

data class Address (

        @PrimaryKey (autoGenerate = true)
        var id_address: Int,

        var number: Int?,
        var street: String?,
        var zip_code: String?,
        var city: String,
        var country: String

)