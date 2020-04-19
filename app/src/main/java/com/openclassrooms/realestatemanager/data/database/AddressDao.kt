package com.openclassrooms.realestatemanager.data.database

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.add_edit.Address

@Dao
interface AddressDao {

    @Query("SELECT * FROM Address")
    fun getAllAddress(): LiveData<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAddress(address: Address): Long

    @Query("SELECT * FROM Address WHERE idProperty= :idProperty")
    suspend fun getAddressOfOneProperty(idProperty: String): Address

    //-- Function for Content Provider --//
    @Query("SELECT * FROM Address WHERE id_address = :id_address")
    fun getAddressWithCursor(id_address: String): Cursor
}