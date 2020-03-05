package com.openclassrooms.realestatemanager

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassrooms.realestatemanager.show.MainActivity

open class BaseActivity: AppCompatActivity() {

    fun getCurrentUser(): FirebaseUser {
        return FirebaseAuth.getInstance().currentUser!!
    }

    fun onFailureListener(): OnFailureListener {
        return OnFailureListener { exception ->
            Log.d("Error", exception.printStackTrace().toString())
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage("Error")
                    .setNegativeButton("Ok") { dialog, which ->

                    }
                    .create().show()
        }
    }

}
