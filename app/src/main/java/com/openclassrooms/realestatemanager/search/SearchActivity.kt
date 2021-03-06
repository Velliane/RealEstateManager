package com.openclassrooms.realestatemanager.search

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.AgentSpinnerAdapter
import com.openclassrooms.realestatemanager.add_edit.NearbyAdapter
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import com.openclassrooms.realestatemanager.utils.getNearbyList
import kotlinx.android.synthetic.main.activity_search.*
import java.text.NumberFormat
import java.util.*


class SearchActivity : AppCompatActivity(), View.OnClickListener {

    /** Views */
    private lateinit var nachoType: NachoTextView
    private lateinit var nachoLocation: NachoTextView
    private lateinit var nachoNearby: NachoTextView
    private lateinit var rangeRooms: CrystalRangeSeekbar
    private lateinit var roomsPreview: TextView
    private lateinit var rangeBedRooms: CrystalRangeSeekbar
    private lateinit var bedroomsPreview: TextView
    private lateinit var rangePrice: CrystalRangeSeekbar
    private lateinit var pricePreview: TextView
    private lateinit var spinnerAgent: Spinner
    /** ViewModel */
    private lateinit var searchViewModel: SearchViewModel
    /** Default value */
    private var roomsMinValue = 1
    private var roomsMaxValue = 8
    private var bedroomsMinValue = 1
    private var bedroomsMaxValue = 6
    private var priceMinValue = 0
    private var priceMaxValue = 100000000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.search_button)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //-- Configure ViewModel --//
        val searchViewModelFactory = Injection.provideViewModelFactory(this)
        searchViewModel = ViewModelProviders.of(this, searchViewModelFactory).get(SearchViewModel::class.java)
        bindViews()
        manageRangeSeekBar()
        search_button.setOnClickListener(this)
    }

    private fun bindViews() {
        spinnerAgent = findViewById(R.id.agent_spinner)
        //-- Nachos --//
        nachoType = findViewById(R.id.search_spinner_type)
        nachoType.setAdapter(ArrayListStringAdapter(this, searchViewModel.getTypesResList()))
        nachoLocation = findViewById(R.id.search_nachos_location)
        nachoLocation.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        nachoNearby = findViewById(R.id.search_nachos_nearby)
        nachoNearby.setAdapter(NearbyAdapter(this, getNearbyList()))
        //-- RangeSeekBars --//
        rangeRooms = findViewById(R.id.search_rooms_seek_bar)
        roomsPreview = findViewById(R.id.rooms_view)
        rangeBedRooms = findViewById(R.id.search_bedrooms_seek_bar)
        bedroomsPreview = findViewById(R.id.bedrooms_view)
        rangePrice = findViewById(R.id.search_price_seek_bar)
        pricePreview = findViewById(R.id.price_preview)

        searchViewModel.getAllUser()
        searchViewModel.userListLiveData.observe(this, androidx.lifecycle.Observer {
            val adapterAgent = AgentSpinnerAdapter(this, it)
            spinnerAgent.adapter = adapterAgent
        })
    }

    private fun manageRangeSeekBar() {
        rangeRooms.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            roomsMinValue = minValue.toInt()
            roomsMaxValue = maxValue.toInt()
            val text = getString(R.string.search_preview_rooms, minValue.toString(), maxValue.toString())
            roomsPreview.text = text
        }
        rangeBedRooms.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            bedroomsMinValue = minValue.toInt()
            bedroomsMaxValue = maxValue.toInt()
            val text = getString(R.string.search_preview_rooms, minValue.toString(), maxValue.toString())
            bedroomsPreview.text = text
        }
        rangePrice.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            priceMinValue = minValue.toInt()
            priceMaxValue = maxValue.toInt()
            val format = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 0
            format.currency = Currency.getInstance("USD")
            val min = format.format(minValue)
            val max = format.format(maxValue)
            val text = getString(R.string.search_preview_rooms, min, max)
            pricePreview.text = text
        }
    }

    override fun onClick(item: View?) {
        if (item == search_button) {
            var agent = ""
            searchViewModel.userListLiveData.observe(this, androidx.lifecycle.Observer {

                agent = it[spinnerAgent.selectedItemPosition].userId
            })
            val query = searchViewModel.constructQueryResearch(agent, priceMinValue, priceMaxValue, nachoType.chipValues, nachoLocation.chipValues, nachoNearby.chipValues, roomsMinValue, roomsMaxValue, bedroomsMinValue, bedroomsMaxValue)
            val intent = Intent()
            intent.putExtra(Constants.SEARCH_QUERY, query)
            setResult(Constants.RC_SEARCH, intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
