package com.openclassrooms.realestatemanager.controller.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.controller.fragment.EditDialogFragment

class MainActivity : AppCompatActivity(), View.OnClickListener{

    /** Toolbar*/
    private lateinit var toolbar: Toolbar
    /** Drawer */
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerMenu: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //--Views--//
        toolbar = findViewById(R.id.main_toolbar)
        drawerLayout = findViewById(R.id.main_drawer_container)
        drawerMenu = findViewById(R.id.main_drawer)

        configureToolbar()
        configureDrawerLayout()

    }

    override fun onClick(v: View?) {
        // TODO
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.toolbar_menu_add -> {
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = supportFragmentManager.findFragmentByTag("edit")
                if(fragment != null){
                    transaction.remove(fragment)
                }
                transaction.addToBackStack(null)

                val dialogFragment = EditDialogFragment.newInstance("content")
                dialogFragment.show(transaction, "edit")
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
                .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                .commit()

    }


}