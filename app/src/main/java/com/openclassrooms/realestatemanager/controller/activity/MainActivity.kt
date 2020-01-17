package com.openclassrooms.realestatemanager.controller.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ListAdapter
import com.openclassrooms.realestatemanager.controller.fragment.DetailsFragment
import com.openclassrooms.realestatemanager.controller.fragment.ListFragment
import com.openclassrooms.realestatemanager.controller.fragment.MapViewFragment
import com.openclassrooms.realestatemanager.utils.getScreenOrientation


class MainActivity : AppCompatActivity(), View.OnClickListener, ListAdapter.OnItemClickListener, BottomNavigationView.OnNavigationItemSelectedListener {

    /** Toolbar*/
    private lateinit var toolbar: Toolbar
    /** Drawer */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: NavigationView
    /** Boolean isTablet */
    private var isLandscape: Boolean = false
    /**Bottom Navigation View */
    private lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //-- Check the screen orientation --//
        isLandscape = getScreenOrientation(resources.configuration.orientation)

        //-- Views --//
        toolbar = findViewById(R.id.main_toolbar)
        drawerLayout = findViewById(R.id.main_drawer_container)
        drawerMenu = findViewById(R.id.main_drawer)
        bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        //-- Configuration --//
        configureToolbar()
        configureDrawerLayout()

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_list_view -> {
                showListFragment()
                return true
            }
            R.id.action_map_view -> {
                showMapFragment()
                return true
            }
        }
        return false
    }

    //-- TOOLBAR MENU --//
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.toolbar_menu_add -> {
                val intent = Intent(this, EditActivity::class.java)
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
    private fun configureToolbar() {
        setSupportActionBar(toolbar)
    }

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
            val detailsFragment = DetailsFragment.newInstance()
            addFragment(detailsFragment, R.id.container_fragment_details)
        }
    }

    private fun showMapFragment() {
        val mapFragment = MapViewFragment.newInstance()
        addFragment(mapFragment, R.id.container_fragment_list)

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


}