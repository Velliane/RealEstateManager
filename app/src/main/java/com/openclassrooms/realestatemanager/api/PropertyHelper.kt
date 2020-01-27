package com.openclassrooms.realestatemanager.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.Constants

fun getPropertiesCollection(): CollectionReference {
    return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_PROPERTIES)
}

fun createProperty(id_property: String, type: String, price: Int, surface: Int, rooms_nbr: Int, bath_nbr: Int, bed_nbr: Int, description: String, in_sale: Boolean): Task<Void>{
    val newProperty = Property(id_property, type, price, surface, rooms_nbr, bath_nbr, bed_nbr, description, in_sale)
    return getPropertiesCollection().document(id_property).set(newProperty)
}

fun getProperty(id_property: Int): Task<DocumentSnapshot> {
    return getPropertiesCollection().document(id_property.toString()).get()
}

fun updatePropertyInformation(id_property: String, type: String, price: Int, surface: Int, rooms_nbr: Int, bath_nbr: Int, bed_nbr: Int, description: String, in_sale: Boolean): Task<Void>{
    return getPropertiesCollection().document(id_property).update("type", type, "price", price, "surface", surface, "rooms_nbr", rooms_nbr, "bath_nbr", bath_nbr, "bed_nbr", bed_nbr, "description", description, "in_sale", in_sale)
}