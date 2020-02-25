package com.openclassrooms.realestatemanager.data

import android.util.Log
import com.openclassrooms.realestatemanager.data.database.AddressDao
import com.openclassrooms.realestatemanager.data.database.PropertyDao
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.utils.compareByDate

/**
 * Repository with suspend function to update RoomDatabase with Firestore Data
 */

class FirestoreDataRepository(private val propertyDao: PropertyDao, private val addressDao: AddressDao) {

    /**
     * Get all properties from Firestore
     * @return A list of Property
     */
    private suspend fun getAllPropertyFromFirestore(): List<Property> {
        val list = ArrayList<Property>()
        val propertyHelper = PropertyHelper()
        val listDocument = propertyHelper.getAllProperties()
        for(document in listDocument){
            val place = document.toObject<Property>(Property::class.java)
            Log.d("Place from firebase", document.data.toString())
            list.add(place!!)
        }
        return list
    }

    /**
     * Get address of one property
     * @param id_property the id of the property
     */
    private suspend fun getAddressFromFirestore(id_property: String): Address {
        val linkHelper = LinkHelper()
        val addressHelper = AddressHelper()
        var id: String
        var address = Address()
        val querySnapshot = linkHelper.getDocument(id_property)
        for(query in querySnapshot!!.documents){
            id = query.id
            val document = addressHelper.getAddressById(id)
            address = document!!.toObject(Address::class.java)!!
        }
        return address
    }

    /**
     * Update RoomDatabase with Firestore Data
     * @param listRoom the list of properties found in RoomDatabase
     */
    suspend fun updateDatabase(listRoom: List<Property>?){
        val list = getAllPropertyFromFirestore()
        if(listRoom == null){
            for(property in list){
                propertyDao.addProperty(property)
                addressDao.addAddress(getAddressFromFirestore(property.id_property))
            }
        }else{
            for (property in list){
                var foundId = false
                for(propertyRoom in listRoom){
                    if (property.id_property == propertyRoom.id_property){
                        if (property.date != propertyRoom.date){
                            val updatedProperty = compareByDate(propertyRoom, property)
                            propertyDao.addProperty(updatedProperty)
                            addressDao.addAddress(getAddressFromFirestore(updatedProperty.id_property))
                            foundId = true
                            break
                        }
                    }
                }
                if(!foundId){
                    propertyDao.addProperty(property)
                    addressDao.addAddress(getAddressFromFirestore(property.id_property))
                }
            }
        }
    }


}