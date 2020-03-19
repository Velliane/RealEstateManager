package com.openclassrooms.realestatemanager.add_edit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity


data class Property(

        @PrimaryKey
        @ColumnInfo (name = "id_property")
        var id_property: String = "",

        @ColumnInfo (name = "agent")
        var agent: String = "",

        @ColumnInfo (name = "type")
        var type: String = "",
        @ColumnInfo (name = "price")
        var price: Int = 0,
        @ColumnInfo (name = "surface")
        var surface: Int? = 0,
        @ColumnInfo (name = "rooms_nbr")
        var rooms_nbr: Int? = 0,
        @ColumnInfo (name = "bath_nbr")
        var bath_nbr: Int? = 0,
        @ColumnInfo (name = "bed_nbr")
        var bed_nbr: Int? = 0,
        @ColumnInfo (name = "description")
        var description: String? = "",
        @ColumnInfo (name = "in_sale")
        var  in_sale: Boolean = true,
        @ColumnInfo (name = "nearby")
        val nearby: String? = "",

        @ColumnInfo (name = "created_date")
        val created_date: String = "",
        @ColumnInfo (name = "sold_date")
        val sold_date: String? = "",

        @ColumnInfo (name = "date")
        val date: String = ""

)