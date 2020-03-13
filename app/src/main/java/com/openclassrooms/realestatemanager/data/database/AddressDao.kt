package com.openclassrooms.realestatemanager.data.database

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
    fun getAddressOfOneProperty(idProperty: String): Address


}