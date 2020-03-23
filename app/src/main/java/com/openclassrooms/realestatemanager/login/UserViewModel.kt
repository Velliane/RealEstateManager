package com.openclassrooms.realestatemanager.login

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.openclassrooms.realestatemanager.utils.Constants

/**
 * ViewModel for LoginActivity
 * Save User in PropertyDatabase and Firestore, and save user's id in SharedPreferences
 */

class UserViewModel(private val userDataRepository: UserDataRepository): ViewModel() {

    private val userHelper = UserHelper()

    fun saveUser(user: User, context: Context, sharedPreferences: SharedPreferences) {
        userDataRepository.addUser(user)
        userHelper.createUser(user.userId, user.name, user.email, user.photo!!).addOnFailureListener(OnFailureListener { exception ->
            Log.d("Error", exception.printStackTrace().toString())
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Error")
                    .setNegativeButton("Ok") { dialog, which -> }
                    .create().show()
        })
        //-- Save userId in SharedPreferences --//
        sharedPreferences.edit().putString(Constants.PREF_ID_USER, user.userId).apply()
    }

}