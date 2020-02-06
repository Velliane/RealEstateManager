package com.openclassrooms.realestatemanager.controller.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.openclassrooms.realestatemanager.adapters.ListPropertyAdapter
import com.openclassrooms.realestatemanager.controller.fragment.DetailsFragment
import com.openclassrooms.realestatemanager.controller.fragment.ListFragment
import com.openclassrooms.realestatemanager.controller.fragment.MapViewFragment
import com.openclassrooms.realestatemanager.model.User
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Utils
import com.openclassrooms.realestatemanager.utils.getScreenOrientation
import com.openclassrooms.realestatemanager.view_model.UserViewModel
import com.openclassrooms.realestatemanager.view_model.injections.Injection

/**
 * The activity that contains ListFragment, MapViewFragment and DetailsFragment
 */
class MainActivity : BaseActivity(), View.OnClickListener, ListPropertyAdapter.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

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
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //-- Check the screen orientation --//
        isLandscape = getScreenOrientation(resources.configuration.orientation)

        //-- Get Shared Preferences --//
        sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        //-- Configuration --//
        bindViews()
        configureDrawerLayout()
        configureUserViewModel()
        configureDrawer()
        bottomNavigationView.selectedItemId = R.id.action_list_view

        //-- Show Fragments --//
        showListFragment()


        if (isLandscape) {
            val detailsFragment = DetailsFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.container_fragment_details, detailsFragment, "DETAILS").commit()
        } else {
            val fragment = supportFragmentManager.findFragmentByTag("DETAILS")
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }

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
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.drawer_menu_logout -> {
                logOut()
            }
        }
        return false
    }

    //-- TOOLBAR MENU --//
    /**
     * When click on a Toolbar's item
     * @param item: the item clicked
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.toolbar_menu_add -> {
                val intent = Intent(this, EditAddActivity::class.java)
                intent.putExtra(Constants.PROPERTY_ID, "")
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

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.toolbar_menu_modify)?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    //-- CONFIGURATION --//
    private fun bindViews() {
        toolbar = findViewById(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.main_drawer_container)
        drawerMenu = findViewById(R.id.main_drawer)
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    /**
     * Configure the layout that contain the DrawerMenu
     */
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

    /**
     * Configure the DrawerMenu, add a header
     */
    private fun configureDrawer() {
        drawerMenu.setNavigationItemSelectedListener(this)

        val view = drawerMenu.getHeaderView(0)
        photo = view.findViewById(R.id.header_photo)
        name = view.findViewById(R.id.header_name)

        //-- Update Views with user's info --//
        if (Utils.isInternetAvailable(this)) {
            //-- If connected to internet, get user's information from Firebase --//
            if (getCurrentUser().photoUrl != null) {
                Glide.with(this).load(getCurrentUser().photoUrl).apply(RequestOptions.circleCropTransform()).centerCrop().into(photo)
            }
            name.text = getCurrentUser().displayName

        } else {
            //-- If not connected, get users' information from Room --//
            val id = sharedPreferences.getString(Constants.PREF_ID_USER, "")
            userViewModel.getUserById(id!!).observe(this, Observer<User> {
                if (it.photo != null) {
                    Glide.with(this).load(it.photo).apply(RequestOptions.circleCropTransform()).centerCrop().into(photo)
                }
                name.text = it.name
            })
        }
    }

    private fun configureUserViewModel() {
        val viewModelFactory = Injection.provideUserViewModelFactory(this)
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel::class.java)
    }

    //-- FRAGMENTS --//
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_fragment_list, fragment)
                .commit()

//                supportFragmentManager.beginTransaction()
//                .replace(container, fragment, fragment.javaClass.simpleName.toString())
//                .commit()
    }

    private fun showListFragment() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_list)
        if (fragment == null) {
            val listFragment = ListFragment.newInstance()
            addFragment(listFragment)
        }
    }

    private fun showMapFragment() {
        val mapViewFragment = MapViewFragment.newInstance()
        addFragment(mapViewFragment)
    }


    //-- LIFE CYCLE --//
    override fun onResume() {
        super.onResume()
        isLandscape = getScreenOrientation(resources.configuration.orientation)
        if (isLandscape) {
            if (bottomNavigationView.selectedItemId == R.id.action_map_view) {
                showMapFragment()
            }
        }
    }

    override fun onBackPressed() {
        if (!isLandscape) {
            showListFragment()
        }
    }

    //-- LOGOUT --//
    private fun logOut() {
        AuthUI.getInstance().signOut(this).addOnCompleteListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onClick(v: View?) {
        // TODO
    }

    override fun onItemClicked(id: String, position: Int) {
        //TODO
    }
}