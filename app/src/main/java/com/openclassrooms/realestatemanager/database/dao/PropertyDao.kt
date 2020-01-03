package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.openclassrooms.realestatemanager.model.Property


@Dao
interface PropertyDao {

    @Query("SELECT * FROM Property")
    fun getAllProperties(): LiveData<List<Property>>
}