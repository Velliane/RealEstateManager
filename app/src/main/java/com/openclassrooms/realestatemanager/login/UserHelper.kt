package com.openclassrooms.realestatemanager.login

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.realestatemanager.utils.Constants
import kotlinx.coroutines.tasks.await

class UserHelper {

    private fun getUsersCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS)
    }

    fun createUser(userId: String, name: String, email: String, photo: String): Task<Void> {
        val newUser = User(userId, name, email, photo)
        return getUsersCollection().document(userId).set(newUser)
    }

    fun getUser(userId: String): Task<DocumentSnapshot> {
        return getUsersCollection().document(userId).get()
    }

    fun deleteUser(userId: String): Task<Void> {
        return getUsersCollection().document(userId).delete()
    }

    suspend fun getAllUsers(): List<DocumentSnapshot> {
        return getUsersCollection().get().await().documents
    }
}