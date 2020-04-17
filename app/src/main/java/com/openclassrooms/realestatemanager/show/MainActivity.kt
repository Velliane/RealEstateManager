package com.openclassrooms.realestatemanager.show

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.simulator.SimulatorActivity
import com.openclassrooms.realestatemanager.add_edit.EditAddActivity
import com.openclassrooms.realestatemanager.search.SearchActivity
import com.openclassrooms.realestatemanager.settings.SettingsActivity
import com.openclassrooms.realestatemanager.show.detail.DetailsFragment
import com.openclassrooms.realestatemanager.show.list.ListFragment
import com.openclassrooms.realestatemanager.show.list.ListPropertyAdapter
import com.openclassrooms.realestatemanager.show.map.MapViewFragment
import com.openclassrooms.realestatemanager.utils.*

/**
 * The activity that contains ListFragment, MapViewFragment and DetailsFragment
 */
class MainActivity : BaseActivity(), ListPropertyAdapter.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    /** Toolbar*/
    private lateinit var toolbar: Toolbar
    /** Drawer */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: NavigationView
    /** Boolean isTablet */
    private var isLandscape: Boolean = false
    private var querySearch: String? = ""
    /**Bottom Navigation View */
    private lateinit var bottomNavigationView: BottomNavigationView
    /** ViewModel */
    private lateinit var mainViewModel: MainViewModel
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
        configureViewModel()
        if(checkExternalStoragePermissions() && Utils.isInternetAvailable(this)) {
            mainViewModel.updateDatabase()
            showUploadingProgress()
        }

        //-- Configuration --//
        bindViews()
        configureDrawerLayout()
        if (savedInstanceState == null) {
            sharedPreferences.edit().putString(Constants.PREF_ID_PROPERTY, "").apply()
            bottomNavigationView.selectedItemId = R.id.action_list_view
        }
        configureDrawer()
        showFragments(savedInstanceState)
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
            R.id.drawer_menu_simulator -> {
                startActivity(Intent(this, SimulatorActivity::class.java))
            }
            R.id.drawer_menu_logout -> {
                mainViewModel.logOut()
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data == null){
            return
        }else {
            querySearch = data.getStringExtra(Constants.SEARCH_QUERY)
            val fragment = supportFragmentManager.findFragmentById(R.id.container_fragment_list)
            (fragment as? ListFragment)?.refreshQuery(querySearch!!)
        }
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
            R.id.toolbar_menu_search -> {
                startActivityForResult(Intent(this, SearchActivity::class.java), Constants.RC_SEARCH)
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
        val id = sharedPreferences.getString(Constants.PREF_ID_USER, "")
        mainViewModel.updateHeader(getCurrentUser().displayName.toString(), getCurrentUser().photoUrl.toString(), getCurrentUser().email.toString(), id).observe(this, Observer {
            name.text = it.name
            Glide.with(applicationContext).load(it.photo).apply(RequestOptions.circleCropTransform()).centerCrop().into(photo)
        })
    }


    private fun configureViewModel() {
        val viewModelFactory = Injection.provideViewModelFactory(this)
        mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
    }


    //-- FRAGMENTS --//
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container_fragment_list, fragment)
                .commit()
    }

    private fun showFragments(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            showListFragment()
            if (isLandscape) {
                val detailsFragment = DetailsFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment_details, detailsFragment, "DETAILS").commit()
            }
        }
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
            }else{
                val fragment = ListFragment.newInstance()
                val details = DetailsFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment_list, fragment).commit()
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment_details, details).commit()
            }
        }
    }

    override fun onBackPressed() {
        if (!isLandscape) {
            if (bottomNavigationView.selectedItemId == R.id.action_list_view) { showListFragment()
            }else{ showMapFragment() }
            bottomNavigationView.visibility = View.VISIBLE
            invalidateOptionsMenu()
        }
    }

    override fun onItemClicked(id: String, position: Int) {
        sharedPreferences.edit().putString(Constants.PREF_ID_PROPERTY, id).apply()
    }

    private fun checkExternalStoragePermissions(): Boolean {
        return if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            true
        }else{ //-- Request permissions --//
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), Constants.RC_PERMISSION_LOCATION)
            false
        }
    }

    /**
     * Show Dialog with ProgressBar while uploading image in Firebase Storage
     */
    private fun showUploadingProgress(){
        val dialog = ProgressDialog(this)
        dialog.setTitle("Mise à jour de la base de données")
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.show()
        mainViewModel.progressUploadLiveData.observe(this, Observer {
            dialog.progress = it
            if(it == 100){
                dialog.dismiss()
            }
        })
    }
}