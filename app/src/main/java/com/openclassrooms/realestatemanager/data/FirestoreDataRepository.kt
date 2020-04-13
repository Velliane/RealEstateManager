package com.openclassrooms.realestatemanager.data

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.database.AddressDao
import com.openclassrooms.realestatemanager.data.database.PropertyDao
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDao
import com.openclassrooms.realestatemanager.login.UserHelper
import com.openclassrooms.realestatemanager.search.TypeEnum
import com.openclassrooms.realestatemanager.utils.compareByDate
import java.io.File

/**
 * Repository with suspend function to update RoomDatabase with Firestore Data
 */

open class FirestoreDataRepository(private val context: Context, private val propertyDao: PropertyDao, private val addressDao: AddressDao, private val userDao: UserDao) {

    /**
     * Get all properties from Firestore
     * @return A list of Property
     */
    open suspend fun getAllPropertyFromFirestore(): List<Property> {
        val list = ArrayList<Property>()
        val propertyHelper = PropertyHelper()
        val listDocument = propertyHelper.getAllProperties()
        for (document in listDocument) {
            val place = document.toObject(Property::class.java)
            Log.d("Place from firebase", document.data.toString())
            list.add(place!!)
        }
        return list
    }

    /**
     * Get address of one property
     * @param id_property the id of the property
     */
    open suspend fun getAddressFromFirestore(id_property: String): Address {
        val linkHelper = LinkHelper()
        val addressHelper = AddressHelper()
        var id: String
        var address = Address()
        val querySnapshot = linkHelper.getDocument(id_property)
        for (query in querySnapshot!!.documents) {
            id = query.id
            val document = addressHelper.getAddressById(id)
            address = document!!.toObject(Address::class.java)!!
        }
        return address
    }

    open suspend fun getAllUsersFromFirestore(): List<User> {
        val list = ArrayList<User>()
        val userHelper = UserHelper()
        val documents = userHelper.getAllUsers()
        for (document in documents) {
            val user = document.toObject(User::class.java)
            list.add(user!!)
        }
        return list
    }

    /**
     * Update RoomDatabase with Firestore Data
     * @param listRoom the list of properties found in RoomDatabase
     */
    suspend fun updateDatabase(listRoom: List<Property>?, listUser: List<User>?) {
        //-- Properties --//
        val list = getAllPropertyFromFirestore()
        //-- If database is empty, add all datas from Firestore --//
        if (listRoom == null) {
            for (property in list) {
                saveDatas(property)
            }
        } else {
            //-- Check if property already exist, and update it --//
            for (property in list) {
                var foundId = false
                for (propertyRoom in listRoom) {
                    if (property.id_property == propertyRoom.id_property) {
                        if (property.date != propertyRoom.date) {
                            val updatedProperty = compareByDate(propertyRoom, property)
                            saveDatas(updatedProperty)
                            foundId = true
                            break
                        }
                    }
                }
                if (!foundId) {
                    saveDatas(property)
                }
            }
        }
        //-- Users --//
        val listUserFromFirestore = getAllUsersFromFirestore()
        if (listUser == null) {
            for (user in listUserFromFirestore) {
                userDao.addUser(user)
            }
        } else {
            for (user in listUserFromFirestore) {
                var foundId = false
                for (userRoom in listUser) {
                    if (user.userId == userRoom.userId) {
                        foundId = true
                        break
                    }
                }
                if (!foundId) {
                    userDao.addUser(user)
                }
            }
        }
    }

    private suspend fun saveDatas(property: Property){
        propertyDao.addProperty(property)
        propertyDao.updatePropertyType(context.getString(TypeEnum.valueOf(property.type).res), property.id_property)
        addressDao.addAddress(getAddressFromFirestore(property.id_property))
        getAllPhotos(property.id_property)
    }

    private fun getAllPhotos(id: String) {
        val storage = FirebaseStorage.getInstance()
        val storageReference = storage.getReference("/images/$id")
        storageReference.listAll().addOnSuccessListener {
            it.items.forEach { ref ->
                val name = ref.name
                val root = Environment.getExternalStorageDirectory().path + "/RealEstateManager/"
                val dir = File(root)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val folder = File(root, "$id/")
                if (!folder.exists()) {
                    folder.mkdirs()
                }
                val file = File(folder, "$name.jpg")
                if (!file.exists()) {
                    file.createNewFile()
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        storage.getReferenceFromUrl(uri.toString()).getFile(file).addOnSuccessListener {
                            Log.d("File", file.absolutePath)
                        }
                    }
                }
            }
        }
    }


}