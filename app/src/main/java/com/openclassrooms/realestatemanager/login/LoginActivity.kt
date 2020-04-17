package com.openclassrooms.realestatemanager.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.show.MainActivity
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection

/**
 * This Activity allows the user to create an account or log in with his email, using Firebase Authentication
 * When an account is created, user's info are saved in PropertyDatabase and Firestore
 */
class LoginActivity : BaseActivity(), View.OnClickListener{

    /** Views */
    private lateinit var connexionBtn: Button
    private lateinit var layout: ConstraintLayout
    /** UserViewModel */
    private lateinit var userViewModel: UserViewModel
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        connexionBtn = findViewById(R.id.auth_btn_connexion)
        connexionBtn.setOnClickListener(this)
        layout = findViewById(R.id.auth_container)
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        configureUserViewModel()
    }

    override fun onClick(v: View?) {
        when(v){
            connexionBtn -> { signIn() }
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

    //-- HANDLE RESPONSE OF SIGN IN--//
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == Constants.RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                Snackbar.make(layout, getString(R.string.login_connect_succeed) , Snackbar.LENGTH_SHORT).show()
                //-- Create User and save it in Room and Firebase --
                val user = User(getCurrentUser().uid, getCurrentUser().displayName!!, getCurrentUser().email!!, getCurrentUser().photoUrl.toString())
                userViewModel.saveUser(user, this, sharedPreferences)
                //-- Start MainActivity --//
                startActivity(Intent(this, MainActivity::class.java))
            }else{
                when {
                    response == null -> Snackbar.make(layout, getString(R.string.login_connect_canceled) , Snackbar.LENGTH_SHORT).show()
                    response.error?.errorCode == ErrorCodes.NO_NETWORK -> Snackbar.make(layout, getString(R.string.login_no_network), Snackbar.LENGTH_SHORT).show()
                    response.error?.errorCode == ErrorCodes.UNKNOWN_ERROR -> Snackbar.make(layout, getString(R.string.login_error_unknown), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //-- CONFIGURATION --//
    private fun configureUserViewModel(){
        val viewModelFactory = Injection.provideViewModelFactory(this)
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)
    }
}
