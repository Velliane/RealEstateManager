package com.openclassrooms.realestatemanager.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.openclassrooms.realestatemanager.property.Address
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.utils.Constants
import kotlinx.coroutines.tasks.await
import org.threeten.bp.LocalDateTime
import java.lang.StringBuilder

open class PropertyHelper {


        open fun getPropertiesCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PROPERTIES)
        }


        open fun createProperty(id_property: String, newProperty: Property): Task<Void>{
            //val newProperty = Property(id_property, type, price, surface, rooms_nbr, bath_nbr, bed_nbr, description, in_sale, dateTime)
            return getPropertiesCollection().document(id_property).set(newProperty)
        }


        open suspend fun getAddress(id_property: String): QuerySnapshot? {
            val sb = StringBuilder()
            sb.append(id_property)
            sb.append("address")
            val id = sb.toString()
            return getPropertiesCollection().document("2020-02-10T16:09:18").collection(
                    "address").whereEqualTo("id_address","2020-02-10T16:09:18address").get().await()
        }

        open suspend fun getAllProperties(): List<DocumentSnapshot> {
            val snapshot = getPropertiesCollection().get().await()
            return snapshot.documents
        }


        open fun getProperty(id_property: String): Task<DocumentSnapshot> {
            return getPropertiesCollection().document(id_property).get()
        }

}

