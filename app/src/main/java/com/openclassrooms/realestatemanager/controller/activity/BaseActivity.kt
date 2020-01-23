package com.openclassrooms.realestatemanager.controller.activity

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

open class BaseActivity: AppCompatActivity() {

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun onFailureListener(): OnFailureListener {
        return OnFailureListener { exception ->
            Log.d("Error", exception.printStackTrace().toString())
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage("Error")
                    .setNegativeButton("Ok"){ dialog, which ->

                    }
                    .create().show()
        }
    }


}