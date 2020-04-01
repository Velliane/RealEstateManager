package com.openclassrooms.realestatemanager.add_edit

import android.content.ContentValues
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

        @PrimaryKey
        @ColumnInfo(name = "id_address")
        var id_address: String = "",

        @ColumnInfo(name = "number")
        var number: Int? = 0,
        @ColumnInfo(name = "street")
        var street: String? = "",
        @ColumnInfo(name = "zip_code")
        var zip_code: String? = "",
        @ColumnInfo(name = "city")
        var city: String = "",
        @ColumnInfo(name = "country")
        var country: String = "",

        @ColumnInfo(name = "idProperty")
        var idProperty: String = ""

)

fun addressFromContentValues(values: ContentValues?): Address {
        val address = Address()
        if(values!!.containsKey("id_address")){ address.id_address = values.getAsString("id_address") }
        if(values.containsKey("number")){ address.number = values.getAsInteger("number") }
        if(values.containsKey("street")) { address.street = values.getAsString("street") }
        if(values.containsKey("zip_code")) { address.zip_code = values.getAsString("zip_code") }
        if(values.containsKey("city")) { address.city = values.getAsString("city") }
        if(values.containsKey("country")) { address.country = values.getAsString("country") }
        if(values.containsKey("idProperty")) { address.idProperty = values.getAsString("idProperty") }
        return address
}