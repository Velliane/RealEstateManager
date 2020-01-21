package com.openclassrooms.realestatemanager.controller.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utils.Constants

class LoginActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var connexionBtn: Button
    private lateinit var layout: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        connexionBtn = findViewById(R.id.auth_btn_connexion)
        connexionBtn.setOnClickListener(this)
        layout = findViewById(R.id.auth_container)
    }

    override fun onClick(v: View?) {
        when(v){
            connexionBtn -> {
                signIn()
            }
        }
    }

    private fun signIn(){
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setTheme(R.style.SignInScreen)
                    .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
                    .setIsSmartLockEnabled(false, true)
                    .setLogo(R.drawable.home_icon)
                    .build(),
                    Constants.RC_SIGN_IN)
        }

    //-- HANDLE RESPONSE --//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == Constants.RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                Snackbar.make(layout, "Connexion succeed" , Snackbar.LENGTH_SHORT).show()
                // TODO Add user to firebase
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                when {
                    response == null -> Snackbar.make(layout, "Connexion canceled" , Snackbar.LENGTH_SHORT).show()
                    response?.error?.errorCode == ErrorCodes.NO_NETWORK -> Snackbar.make(layout, "No Network", Snackbar.LENGTH_SHORT).show()
                    response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> Snackbar.make(layout, "Unknown error", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
