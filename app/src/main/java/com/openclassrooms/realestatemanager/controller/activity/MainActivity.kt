package com.openclassrooms.realestatemanager.controller.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.ListAdapter
import com.openclassrooms.realestatemanager.controller.fragment.ListFragment
import com.openclassrooms.realestatemanager.injections.Injection
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.utils.getScreenOrientation
import com.openclassrooms.realestatemanager.view_model.PropertyViewModel

class MainActivity : AppCompatActivity(), View.OnClickListener, ListAdapter.OnItemClickListener {

    /** Toolbar*/
    private lateinit var toolbar: Toolbar
    /** Drawer */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: NavigationView
    /** Boolean isTablet */
    private var isTablet: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isTablet = getScreenOrientation(resources.configuration.orientation)

        //--Views--//
        toolbar = findViewById(R.id.main_toolbar)
        drawerLayout = findViewById(R.id.main_drawer_container)
        drawerMenu = findViewById(R.id.main_drawer)

        configureToolbar()
        configureDrawerLayout()

        showFragment()


    }

    override fun onClick(v: View?) {
        // TODO
    }

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
        toogle.drawerArrowDrawable.color = resources.getColor(R.color.colorAccent)
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
    }



    //-- FRAGMENT --//
    private fun addFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.javaClass.simpleName)
                .commit()

    }

    private fun showFragment(){
        val fragment = ListFragment.newInstance()
        addFragment(fragment)
    }

    override fun onResume() {
        super.onResume()
        isTablet = getScreenOrientation(resources.configuration.orientation)
    }

    override fun onItemClicked(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}