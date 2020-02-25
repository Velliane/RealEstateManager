package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.data.database.AddressDao

class AddressDataRepository(private val addressDao: AddressDao) {

    fun getAllAddress(): LiveData<List<Address>>{
        return addressDao.getAllAddress()
    }

    fun addAddress(address: Address): Long{
        return addressDao.addAddress(address)
    }

    fun getAddressOfOneProperty(idProperty: String): LiveData<Address>{
        return addressDao.getAddressOfOneProperty(idProperty)
    }
}