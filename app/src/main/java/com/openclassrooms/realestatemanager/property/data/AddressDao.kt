package com.openclassrooms.realestatemanager.property.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.property.Address

@Dao
interface AddressDao {

    @Query("SELECT * FROM Address")
    fun getAllAddress(): LiveData<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAddress(address: Address): Long

    @Update
    fun updateAddress(address: Address): Int

    @Query("SELECT * FROM Address WHERE idProperty= :idProperty")
    fun getAddressOfOneProperty(idProperty: String): LiveData<Address>

}