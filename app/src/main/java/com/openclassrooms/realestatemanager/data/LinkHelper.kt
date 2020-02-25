package com.openclassrooms.realestatemanager.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await

open class LinkHelper {

    open fun getLinkCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection("link")
    }

    open fun addLink(address_id: String, list: Map<String,List<String>>): Task<Void> {
        return getLinkCollection().document(address_id).set(list)
    }

    open suspend fun getDocument(property_id: String): QuerySnapshot? {
        return getLinkCollection().whereArrayContains("property_id", property_id).get().await()
    }

}