package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.data.database.AddressDao

open class AddressDataRepository(private val addressDao: AddressDao) {

    fun getAllAddress(): LiveData<List<Address>>{
        return addressDao.getAllAddress()
    }

    open fun addAddress(address: Address): Long{
        return addressDao.addAddress(address)
    }

    open fun getAddressOfOneProperty(idProperty: String): Address {
        return addressDao.getAddressOfOneProperty(idProperty)
    }
}