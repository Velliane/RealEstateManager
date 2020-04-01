package com.openclassrooms.realestatemanager.show.list

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.show.BaseFragment
import com.openclassrooms.realestatemanager.show.detail.DetailsFragment
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import com.openclassrooms.realestatemanager.utils.getScreenOrientation
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Fragment that show the list of properties saved in the PropertyDatabase
 * When click on an item of the list:
 * - if orientation is portrait : the fragment DetailsFragment replace the ListFragment with the details of the property clicked
 * - if orientation is landscape : the fragment DetailsFragment is update with the details of the property clicked
 */

class ListFragment: BaseFragment(), ListPropertyAdapter.OnItemClickListener, View.OnClickListener {

    /** RecyclerView */
    private lateinit var recyclerView: RecyclerView
    /** ViewModel */
    private lateinit var viewModel: ListViewModel
    /** RecyclerView Adapter */
    private lateinit var adapter: ListPropertyAdapter
    /** No Data TextView */
    private lateinit var noDataTxt: TextView
    /** Shared Preferences */
    private lateinit var sharedPreferences: SharedPreferences
    /** Reset Button */
    private lateinit var resetBtn: MaterialButton
    /** Currency */
    private var currencySelected: Int = 0

    companion object {
        fun newInstance(): ListFragment {
            return ListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        bindViews(view)
        sharedPreferences = activity!!.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        currencySelected = sharedPreferences.getInt(Constants.PREF_CURRENCY, 0)
        //-- Configure ViewModel --//
        val viewModelFactory = Injection.provideViewModelFactory(requireContext())
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ListViewModel::class.java)
        showList()
        return view
    }

    /**
     * Update Recycler View with list of property if permission is granted for ReadExternalStorage
     * Show all properties from database or result of research is querySearch is not null
     */
    private fun showList() {
        if(checkExternalStoragePermissions()) {
            viewModel.propertiesLiveData.observe(this, Observer {
                updateView(it)
            })
        }
    }

    /**
     * Refresh list of properties according to search query
     */
    fun refreshQuery(querySearch: String) {
        viewModel.searchInDatabase(querySearch)
        viewModel.resetBtnLiveData.observe(this, Observer {
            resetBtn.visibility = it
        })
    }

    /**
     * Update the data of the RecyclerView with the list of properties
     * List<Property>: the list of Property get from the PropertyDatabase
     */
    private fun updateView(properties: List<PropertyModelForList>?) {
        if(properties == null || properties.isEmpty()){
            recyclerView.visibility = View.INVISIBLE
            noDataTxt.visibility = View.VISIBLE
        }else{
            adapter.setData(properties, currencySelected)
        }
    }

    /**
     * When click on an item of the RecyclerView
     */
    override fun onItemClicked(id: String, position: Int) {
        val fragment = DetailsFragment.newInstance()
        sharedPreferences.edit().putString(Constants.PREF_ID_PROPERTY, id).apply()
        viewModel.propertyClicked(id)

        if(!getScreenOrientation(activity!!.resources.configuration.orientation)){
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_list, fragment).commit()
            val bottomNav = (activity?.parent as? ViewGroup)?.findViewById(R.id.activity_main_bottom_navigation) as? BottomNavigationView
            bottomNav?.visibility = View.GONE
        }else{
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.container_fragment_details, fragment).commit()
        }
    }

    override fun onClick(view: View?) {
        when(view){
            resetBtn -> {
                viewModel.reset()
                viewModel.resetBtnLiveData.observe(this, Observer {
                    resetBtn.visibility = it
                })
            }
        }
    }

    //-- CONFIGURATION --//
    private fun bindViews(view: View){
        resetBtn = view.findViewById(R.id.reset_research)
        resetBtn.setOnClickListener(this)
        recyclerView = view.findViewById(R.id.fragment_list_recycler_view)
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.HORIZONTAL))
        recyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
        adapter = ListPropertyAdapter(this, context!!)
        recyclerView.adapter = adapter
        noDataTxt = view.findViewById(R.id.fragment_list_no_data)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if(!getScreenOrientation(resources.configuration.orientation)){
            menu.findItem(R.id.toolbar_menu_modify).isVisible = false
        }
    }


}