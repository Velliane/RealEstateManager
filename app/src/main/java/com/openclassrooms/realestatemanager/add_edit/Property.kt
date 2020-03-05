package com.openclassrooms.realestatemanager.add_edit

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.Converters
import org.threeten.bp.LocalDateTime
import java.io.Serializable

@Entity


data class Property(

        @PrimaryKey
        @ColumnInfo (name = "id_property")
        var id_property: String = "",

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

        @ColumnInfo (name = "date")
        val date: String = ""

)