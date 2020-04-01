package com.openclassrooms.realestatemanager.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.add_edit.NearbyAdapter
import com.openclassrooms.realestatemanager.utils.Constants
import com.openclassrooms.realestatemanager.utils.Injection
import com.openclassrooms.realestatemanager.utils.getNearbyList
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var nachoType: NachoTextView
    private lateinit var nachoLocation: NachoTextView
    private lateinit var nachoNearby: NachoTextView
    private lateinit var rangeRooms: CrystalRangeSeekbar
    private lateinit var roomsPreview: TextView
    private lateinit var rangeBedRooms: CrystalRangeSeekbar
    private lateinit var bedroomsPreview: TextView
    private lateinit var rangePrice: CrystalRangeSeekbar
    private lateinit var pricePreview: TextView

    /** ViewModel */
    private lateinit var searchViewModel: SearchViewModel
    /** Default value */
    private var roomsMinValue = 1
    private var roomsMaxValue = 8
    private var bedroomsMinValue = 1
    private var bedroomsMaxValue = 6
    private var priceMinValue = 0
    private var priceMaxValue = 2000000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.search_button)
        //-- Configure ViewModel --//
        val searchViewModelFactory = Injection.provideViewModelFactory(this)
        searchViewModel = ViewModelProviders.of(this, searchViewModelFactory).get(SearchViewModel::class.java)
        bindViews()
        manageRangeSeekBar()
        search_button.setOnClickListener(this)
    }

    private fun bindViews() {
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
            val text = getString(R.string.search_preview_rooms, minValue.toString(), maxValue.toString())
            pricePreview.text = text
        }
    }

    override fun onClick(item: View?) {
        if (item == search_button) {
            val query = searchViewModel.constructQueryResearch(priceMinValue, priceMaxValue, nachoType.chipValues, nachoLocation.chipValues, nachoNearby.chipValues, roomsMinValue, roomsMaxValue, bedroomsMinValue, bedroomsMaxValue)
            val intent = Intent()
            intent.putExtra(Constants.SEARCH_QUERY, query)
            setResult(Constants.RC_SEARCH, intent)
            finish()
        }
    }

}
