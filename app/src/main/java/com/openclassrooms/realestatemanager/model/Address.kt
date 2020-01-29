package com.openclassrooms.realestatemanager.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity
(
        foreignKeys = [ForeignKey(
                entity = Property::class,
                parentColumns = arrayOf("id_property"),
                childColumns = arrayOf("idProperty"),
                onDelete = CASCADE)]
)

data class Address (

        @PrimaryKey (autoGenerate = true)
        @ColumnInfo(name = "id_address")
        var id_address: Int,

        @ColumnInfo(name = "number")
        var number: Int?,
        @ColumnInfo(name = "street")
        var street: String?,
        @ColumnInfo(name = "zip_code")
        var zip_code: String?,
        @ColumnInfo(name = "city")
        var city: String,
        @ColumnInfo(name = "country")
        var country: String,

        @ColumnInfo(name = "idProperty")
        var idProperty: String

)