package com.openclassrooms.realestatemanager.utils

import android.content.Context
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.database.AddressDao
import com.openclassrooms.realestatemanager.data.database.PropertyDao
import com.openclassrooms.realestatemanager.login.UserDao

class FakeFirestoreDataRepository(private val context: Context, private val propertyDao: PropertyDao, private val addressDao: AddressDao, private val userDao: UserDao): FirestoreDataRepository(context, propertyDao, addressDao, userDao) {

    override suspend fun getAllPropertyFromFirestore(): List<Property> {
        val list = ArrayList<Property>()
        //-- Create Properties and add them to list --//
        val property1 = Property("001", "005","House", 250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25")
        val property2 = Property("002", "025","House", 185000, 75, 3, 1, 2, "Little house", true, "2020-03-09T12:20:25")
        list.add(property1)
        list.add(property2)
        return list
    }

    override suspend fun getAddressFromFirestore(id_property: String): Address {
        var address = Address()
        when(id_property){
            "001" -> address = Address("101", 4, "Bryden Road", "43004", "Columbus", "United States", "001")
            "002" -> address = Address("202", 53,"Livingston Avenue", "43068", "Columbus", "United States", "002")
        }
        return address
    }
}