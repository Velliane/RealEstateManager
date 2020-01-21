package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
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
        @ColumnInfo (name = "id_property")
        var id_property: Int,

        @ColumnInfo (name = "type")
        var type: String,
        @ColumnInfo (name = "price")
        var price: Int,
        @ColumnInfo (name = "surface")
        var surface: Int?,
        @ColumnInfo (name = "rooms_nbr")
        var rooms_nbr: Int?,
        @ColumnInfo (name = "bath_nbr")
        var bath_nbr: Int?,
        @ColumnInfo (name = "bed_nbr")
        var bed_nbr: Int?,
        @ColumnInfo (name = "description")
        var description: String?,

        @ColumnInfo (name = "in_sale")
        var  in_sale: Boolean

)