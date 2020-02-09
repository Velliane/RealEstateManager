package com.openclassrooms.realestatemanager.property.data

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.property.Address

class AddressDataRepository(private val addressDao: AddressDao) {

    fun getAllAddress(): LiveData<List<Address>>{
        return addressDao.getAllAddress()
    }

    fun addAddress(address: Address): Long{
        return addressDao.addAddress(address)
    }

    fun updateAddress(address: Address): Int{
        return addressDao.updateAddress(address)
    }

    fun getAddressOfOneProperty(idProperty: String): LiveData<Address>{
        return addressDao.getAddressOfOneProperty(idProperty)
    }
}