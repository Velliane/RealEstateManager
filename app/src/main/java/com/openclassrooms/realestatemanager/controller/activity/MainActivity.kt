package com.openclassrooms.realestatemanager.controller.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ListAdapter
import com.openclassrooms.realestatemanager.controller.fragment.DetailsFragment
import com.openclassrooms.realestatemanager.controller.fragment.ListFragment
import com.openclassrooms.realestatemanager.controller.fragment.MapViewFragment
import com.openclassrooms.realestatemanager.controller.view.AddressSelector
import com.openclassrooms.realestatemanager.model.User
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.getScreenOrientation
import com.openclassrooms.realestatemanager.view_model.UserViewModel
import com.openclassrooms.realestatemanager.view_model.injections.Injection
import org.w3c.dom.Text


class MainActivity : BaseActivity(), View.OnClickListener, ListAdapter.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    /** Toolbar*/
    private lateinit var toolbar: Toolbar
    /** Drawer */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: NavigationView
    /** Boolean isTablet */
    private var isLandscape: Boolean = false
    /**Bottom Navigation View */
    private lateinit var bottomNavigationView: BottomNavigationView
    /** UserViewModel */
    private lateinit var userViewModel: UserViewModel
    /** Header Views */
    private lateinit var photo: ImageView
    private lateinit var name: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //-- Check the screen orientation --//
        isLandscape = getScreenOrientation(resources.configuration.orientation)

        //-- Views --//
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.main_drawer_container)
        drawerMenu = findViewById(R.id.main_drawer)
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        //-- Configuration --//
        configureDrawerLayout()
        configureDrawer()
        configureUserViewModel()

        bottomNavigationView.selectedItemId = R.id.action_list_view
        //-- Show Fragments --//
        showListFragment()
        showDetailsFragment()


    }

    override fun onClick(v: View?) {
        // TODO
    }

    override fun onItemClicked(id: Int) {
        //TODO
    }

    //-- BOTTOM NAVIGATION AND DRAWER MENU --//
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //-- Bottom navigation --//
            R.id.action_list_view -> {
                showListFragment()
                return true
            }
            R.id.action_map_view -> {
                showMapFragment()
                return true
            }
            //-- Drawer --//
            R.id.drawer_menu_settings -> {
                TODO()
            }
            R.id.drawer_menu_logout -> {
                if(Utils.isInternetAvailable(this)){
                    logOut()
                }else{
                    AlertDialog.Builder(this).setTitle("You are not connected")
                            .setMessage("Beware, you are currently not connected to Internet. If you logout, you will need to be connected again to login. Do you want to continue?")
                            .setPositiveButton("Yes, I want to logout anyway"){dialog, which ->
                                logOut()
                            }
                            .setNegativeButton("Cancel"){dialog, which ->
                               dialog.dismiss()
                            }
                            .create().show()
                }
            }
        }
        return false
    }

    //-- TOOLBAR MENU --//
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.toolbar_menu_add -> {
                val intent = Intent(this, EditAddActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    //-- CONFIGURATION --//

    private fun configureDrawerLayout() {
        val toogle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.main_drawer_open,
                R.string.main_drawer_close
        )
        toogle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.colorAccent)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
    }

    private fun configureDrawer(){
        drawerMenu.setNavigationItemSelectedListener(this)

        val view = drawerMenu.getHeaderView(0)
        photo = view.findViewById(R.id.header_photo)
        name = view.findViewById(R.id.header_name)

        //-- Update Views with user's info --//
        if(Utils.isInternetAvailable(this)){
                if (getCurrentUser().photoUrl != null) {
                    Glide.with(this).load(getCurrentUser().photoUrl).apply(RequestOptions.circleCropTransform()).centerCrop().into(photo)
                }
                name.text = getCurrentUser().displayName

        }else{
            userViewModel.getUserById(getCurrentUser().uid).observe(this, Observer<User> {
                if (it.photo != null) {
                    Glide.with(this).load(it.photo).apply(RequestOptions.circleCropTransform()).centerCrop().into(photo)
                }
                name.text = it.name
            })
        }
    }

    private fun configureUserViewModel(){
        val viewModelFactory = Injection.provideUserViewModelFactory(this)
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)
    }

    //-- FRAGMENTS --//
    private fun addFragment(fragment: Fragment, container: Int) {
        supportFragmentManager.beginTransaction()
                .replace(container, fragment, fragment.javaClass.simpleName)
                .commit()
    }

    private fun showListFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_list)
        if (fragment == null) {
            val listFragment = ListFragment.newInstance()
            addFragment(listFragment, R.id.container_fragment_list)
        }
    }

    private fun showDetailsFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_details)
        if (fragment == null && isLandscape) {
            val detailsFragment = DetailsFragment.newInstance(1)
            addFragment(detailsFragment, R.id.container_fragment_details)
        }
    }

    private fun showMapFragment() {
        val mapViewFragment = MapViewFragment.newInstance()
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_fragment_list, mapViewFragment, mapViewFragment.javaClass.simpleName)
                .commit()
    }

    //-- LIFE CYCLE --//
    override fun onResume() {
        super.onResume()
        isLandscape = getScreenOrientation(resources.configuration.orientation)
        if (bottomNavigationView.selectedItemId == R.id.action_map_view) {
            showMapFragment()
        } else {
            showListFragment()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_details)
        if (fragment == null && !isLandscape) {
            showListFragment()
        }
    }

    //-- LOGOUT --//
    private fun logOut(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener{
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}