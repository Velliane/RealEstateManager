package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.model.Address

@Dao
interface AddressDao {

    @Query("SELECT * FROM Address")
    fun getAllAddress(): LiveData<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAddress(address: Address): Long

    @Update
    fun updateAddress(address: Address): Int

    @Query("SELECT * FROM Address WHERE idUser= :idUser")
    fun getAddressOfOneProperty(idUser: Int): LiveData<Address>

}