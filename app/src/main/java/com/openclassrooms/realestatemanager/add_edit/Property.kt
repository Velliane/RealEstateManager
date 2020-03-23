package com.openclassrooms.realestatemanager.add_edit

import android.content.ContentValues
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
        var nearby: String? = "",

        @ColumnInfo (name = "created_date")
        var created_date: String = "",
        @ColumnInfo (name = "sold_date")
        var sold_date: String? = "",

        @ColumnInfo (name = "date")
        var date: String = ""

)

fun fromContentValues(values: ContentValues?): Property {
        val property = Property()
        if(values!!.containsKey("id_property")){ property.id_property = values.getAsString("id_property") }
        if(values.containsKey("agent")){ property.agent = values.getAsString("agent") }
        if(values.containsKey("type")) { property.type = values.getAsString("type") }
        if(values.containsKey("price")) { property.price = values.getAsInteger("price") }
        if(values.containsKey("surface")) { property.surface = values.getAsInteger("surface") }
        if(values.containsKey("rooms_nbr")) { property.rooms_nbr = values.getAsInteger("rooms_nbr") }
        if(values.containsKey("bath_nbr")) { property.bath_nbr = values.getAsInteger("bath_nbr") }
        if(values.containsKey("bed_nbr")) { property.bed_nbr = values.getAsInteger("bed_nbr") }
        if(values.containsKey("description")) { property.description = values.getAsString("description") }
        if(values.containsKey("in_sale")) { property.in_sale = values.getAsBoolean("in_sale") }
        if(values.containsKey("nearby")) { property.nearby = values.getAsString("nearby") }
        if(values.containsKey("created_date")) { property.created_date = values.getAsString("created_date") }
        if(values.containsKey("sold_date")) { property.sold_date = values.getAsString("sold_date") }
        if(values.containsKey("date")) { property.date = values.getAsString("date") }
        return property
}