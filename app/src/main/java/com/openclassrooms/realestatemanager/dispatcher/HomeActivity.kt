package com.openclassrooms.realestatemanager.dispatcher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.property.show.MainActivity
import com.openclassrooms.realestatemanager.login.LoginActivity

/**
 * Dispatcher : if user is currently connected to its Firebase Account, start MainActivity, else start LoginActivity
 */
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //update database

        Handler().postDelayed({
            kotlin.run {
                    val user = FirebaseAuth.getInstance().currentUser
                    if(user != null){
                        startActivity(Intent(this, MainActivity::class.java))
                    }else{
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
            }
        }, 2000)
    }
}
