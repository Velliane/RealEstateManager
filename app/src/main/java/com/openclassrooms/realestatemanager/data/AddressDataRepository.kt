package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.data.database.AddressDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.runBlocking

open class AddressDataRepository(private val addressDao: AddressDao) {

    fun getAllAddress(): LiveData<List<Address>>{
        return addressDao.getAllAddress()
    }

    open fun addAddress(address: Address): Long{
        return addressDao.addAddress(address)
    }

    open suspend fun getAddressOfOneProperty(idProperty: String): Address {
        return addressDao.getAddressOfOneProperty(idProperty)
    }
}