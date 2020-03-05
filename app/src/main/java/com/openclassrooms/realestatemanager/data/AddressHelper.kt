package com.openclassrooms.realestatemanager.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.utils.Constants
import kotlinx.coroutines.tasks.await

open class AddressHelper {

    open fun getAddressCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_ADDRESS)
    }

    open fun createAddress(id_address: String, newAddress: Address): Task<Void> {
        //val newAddress = Address(id_address, number, street, zip_code, city, country, id_property)
        return getAddressCollection().document(id_address).set(newAddress)
    }

    open suspend fun getAddressById(id_address: String): DocumentSnapshot? {
        return getAddressCollection().document(id_address).get().await()
    }

}