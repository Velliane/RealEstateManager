package com.openclassrooms.realestatemanager.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.property.Property
import java.time.Period

/**
 * DAO Interface to group all CRUD requests for the table Property of the PropertyDatabase
 */

@Dao
interface PropertyDao {

    @Query("SELECT * FROM Property")
    fun getAllProperties(): LiveData<List<Property>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProperty(property: Property): Long

    @Query("SELECT * FROM Property WHERE id_property = :id_property")
    fun getPropertyFromId(id_property: String): LiveData<Property>

    @RawQuery(observedEntities = [Property::class])
    fun searchInDatabase(query: SupportSQLiteQuery): LiveData<List<Property>>
}