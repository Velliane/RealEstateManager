package com.openclassrooms.realestatemanager.property

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
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
//        : Parcelable {
//        constructor(parcel: Parcel) : this(
//                parcel.readString()!!,
//                parcel.readString()!!,
//                parcel.readInt(),
//                parcel.readValue(Int::class.java.classLoader) as? Int,
//                parcel.readValue(Int::class.java.classLoader) as? Int,
//                parcel.readValue(Int::class.java.classLoader) as? Int,
//                parcel.readValue(Int::class.java.classLoader) as? Int,
//                parcel.readString(),
//                parcel.readByte() != 0.toByte(),
//                parcel.readString()!!)
//
//        override fun writeToParcel(parcel: Parcel, flags: Int) {
//                parcel.writeString(id_property)
//                parcel.writeString(type)
//                parcel.writeInt(price)
//                parcel.writeValue(surface)
//                parcel.writeValue(rooms_nbr)
//                parcel.writeValue(bath_nbr)
//                parcel.writeValue(bed_nbr)
//                parcel.writeString(description)
//                parcel.writeByte(if (in_sale) 1 else 0)
//                parcel.writeString(date)
//        }
//
//        override fun describeContents(): Int {
//                return 0
//        }
//
//        companion object CREATOR : Parcelable.Creator<Property> {
//                override fun createFromParcel(parcel: Parcel): Property {
//                        return Property(parcel)
//                }
//
//                override fun newArray(size: Int): Array<Property?> {
//                        return arrayOfNulls(size)
//                }
//        }
//}