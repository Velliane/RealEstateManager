package com.openclassrooms.realestatemanager.property.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.property.Address

open class AddressHelper {

    open fun getAddressCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("address")
    }

    open fun createAddress(id_property: String, address: Address): Task<Void> {
        return getAddressCollection().document(address.id_address).set(address)
    }

}