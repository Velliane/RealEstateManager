package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.database.AddressDao

class FakeAddressDataRepository(private val addressDao: AddressDao): AddressDataRepository(addressDao) {

    override fun addAddress(address: Address): Long {
        return super.addAddress(address)
    }

    override fun getAddressOfOneProperty(idProperty: String): Address {
        return when (idProperty) {
            "001" -> {
                Address("1", 4, "allée des Bleuets", "71500", "Louhans", "France", "001")
            }
            "002" -> {
                Address("2", 22, "rue des Lilas", "01350", "Culoz", "France", "002")
            }
            else -> Address()
        }
    }
}