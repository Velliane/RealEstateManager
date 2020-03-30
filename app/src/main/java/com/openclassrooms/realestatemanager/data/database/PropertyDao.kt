package com.openclassrooms.realestatemanager.data.database

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.openclassrooms.realestatemanager.add_edit.Property

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
    suspend fun getPropertyFromId(id_property: String): Property

    @RawQuery(observedEntities = [Property::class])
    fun searchInDatabase(query: SupportSQLiteQuery): LiveData<List<Property>?>

    @Query("UPDATE Property SET type = :type WHERE id_property = :id_property")
    fun updatePropertyType(type: String, id_property: String): Int

    //-- Function for Content Provider --//
    @Query("SELECT * FROM Property WHERE id_property = :id_property")
    fun getPropertyWithCursor(id_property: String): Cursor
}