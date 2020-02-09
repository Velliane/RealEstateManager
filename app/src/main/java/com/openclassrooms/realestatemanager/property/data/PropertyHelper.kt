package com.openclassrooms.realestatemanager.property.data

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.utils.Constants

open class PropertyHelper {


        open fun getPropertiesCollection(): CollectionReference {
            return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PROPERTIES)
        }


        open fun createProperty(id_property: String, type: String, price: Int, surface: Int, rooms_nbr: Int, bath_nbr: Int, bed_nbr: Int, description: String, in_sale: Boolean, address:String): Task<Void>{
            val newProperty = Property(id_property, type, price, surface, rooms_nbr, bath_nbr, bed_nbr, description, address, in_sale)
            return getPropertiesCollection().document(id_property).set(newProperty)
        }


        open fun getAllProperties(): Task<QuerySnapshot> {
            return getPropertiesCollection().get()
        }


        open fun getProperty(id_property: Int): Task<DocumentSnapshot> {
            return getPropertiesCollection().document(id_property.toString()).get()
        }

}

