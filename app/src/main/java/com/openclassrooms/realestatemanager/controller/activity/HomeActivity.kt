package com.openclassrooms.realestatemanager.controller.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Utils

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        Handler().postDelayed({
            kotlin.run {
                if(Utils.isInternetAvailable(this)){
                    val user = FirebaseAuth.getInstance().currentUser
                    if(user != null){
                        startActivity(Intent(this, MainActivity::class.java))
                    }else{
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }else{
                    startActivity(Intent(this, MainActivity::class.java))
                }

            }
        }, 2000)
    }
}
