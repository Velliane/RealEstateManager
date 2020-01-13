package com.openclassrooms.realestatemanager.database.dao

import android.graphics.LinearGradient
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.model.Property


@Dao
interface PropertyDao {

    @Query("SELECT * FROM Property")
    fun getAllProperties(): LiveData<List<Property>>

    @Insert
    fun addProperty(property: Property): Long

    @Update
    fun updateProperty(property: Property): Int

    @Query("SELECT * FROM Property WHERE id_property = :id_property")
    fun getPropertyFromId(id_property: Int): Int
}