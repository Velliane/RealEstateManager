package com.openclassrooms.realestatemanager.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
//(
//        foreignKeys = [ForeignKey(
//                entity = Address::class,
//                parentColumns = arrayOf("id_address"),
//                childColumns = arrayOf("address_id")
//        )]
//)

data class Property(

        @PrimaryKey(autoGenerate = true)
        var id_property: Int,

        var type: String,
        var price: Int,
        var surface: Int?,
        var rooms_nbr: Int?,
        var bath_nbr: Int?,
        var bed_nbr: Int?,
        var description: String?

)