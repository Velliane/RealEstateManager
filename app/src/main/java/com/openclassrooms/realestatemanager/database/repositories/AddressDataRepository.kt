package com.openclassrooms.realestatemanager.database.repositories

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.database.dao.AddressDao
import com.openclassrooms.realestatemanager.model.Address

class AddressDataRepository(private val addressDao: AddressDao) {

    fun getAllAddress(): LiveData<List<Address>>{
        return addressDao.getAllAddress()
    }

    fun addAddress(address: Address): Long{
        return addressDao.addAddress(address)
    }

    fun udpateAddress(address: Address): Int{
        return addressDao.updateAddress(address)
    }

    fun getAddressOfOneProperty(idUser: Int): LiveData<Address>{
        return addressDao.getAddressOfOneProperty(idUser)
    }
}