package com.openclassrooms.realestatemanager.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.utils.Constants
import kotlinx.coroutines.tasks.await

open class PropertyHelper {


        open fun getPropertiesCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PROPERTIES)
        }

        open fun createProperty(id_property: String, newProperty: Property): Task<Void>{
            return getPropertiesCollection().document(id_property).set(newProperty)
        }

        open suspend fun getAllProperties(): List<DocumentSnapshot> {
            val snapshot = getPropertiesCollection().get().await()
            return snapshot.documents
        }

        open fun getProperty(id_property: String): Task<DocumentSnapshot> {
            return getPropertiesCollection().document(id_property).get()
        }

}

