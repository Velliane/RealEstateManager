package com.openclassrooms.realestatemanager.add_edit

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.jakewharton.threetenabp.AndroidThreeTen
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.show.MainActivity
import com.openclassrooms.realestatemanager.show.detail.PhotosAdapter
import com.openclassrooms.realestatemanager.utils.*
import kotlinx.android.synthetic.main.activity_edit_add.*
import kotlinx.android.synthetic.main.custom_address.*
import org.threeten.bp.LocalDateTime
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

/**
 * Add or Edit a Property
 * If adding, create an Id with LocalDate.now() and save it in PropertyDatabase and Firestore
 * If editing, get the data from the Database with the Id get from the intent, and update it in PropertyDatabase and Firestore
 */
class EditAddActivity : BaseActivity(), View.OnClickListener, PhotosAdapter.OnItemClickListener {

    /** Views */
    private lateinit var spinnerType: Spinner
    private lateinit var spinnerAgent: Spinner
    private lateinit var nearbyNachos: NachoTextView
    private lateinit var price: TextInputEditText
    private lateinit var surface: TextInputEditText
    private lateinit var rooms: TextInputEditText
    private lateinit var description: TextInputEditText
    private lateinit var numberBedrooms: TextInputEditText
    private lateinit var numberBathrooms: TextInputEditText
    private lateinit var number: TextInputEditText
    private lateinit var street: TextInputEditText
    private lateinit var zipCode: TextInputEditText
    private lateinit var city: TextInputEditText
    private lateinit var country: TextInputEditText
    private lateinit var layout: ConstraintLayout
    private lateinit var saveBtn: FloatingActionButton
    private lateinit var addPhotoImg: ImageView
    private lateinit var addPhotoTxt: EditText
    private lateinit var addPhotoBtn: MaterialButton
    private lateinit var deletePhotoBtn: FloatingActionButton
    private lateinit var inSaleRadioBtn: RadioButton
    private lateinit var soldRadioBtn: RadioButton
    private lateinit var inSaleDate: TextView
    private lateinit var soldDate: TextView

    /** TextInputLayout */
    private lateinit var priceContainer: TextInputLayout
    /** ViewModel */
    private lateinit var editDataViewModel: EditDataViewModel
    /** Property Id from DetailsFragment */
    private var propertyId: String = ""
    /** URI of selected image*/
    private var uriImage: Uri? = null
    /** Image's description*/
    private lateinit var desImage: String
    /** Image List */
    private var imageList = ArrayList<Photo>()
    /** RecyclerView and Adapter */
    private lateinit var recyclerView: RecyclerView
    private var photosAdapter = PhotosAdapter(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_add)
        setFinishOnTouchOutside(false)
        AndroidThreeTen.init(this)
        //-- Configuration --//
        configureViewModel()
        bindViews()
        recyclerView.adapter = photosAdapter
        setSpinnerAndNachosAdapters()
        getSaveInstanceState(savedInstanceState)
        //-- Check permission for Write to External Storage --//
        if (!checkPermission()) {
            requestPermission()
        }
        //-- Get Property id from intent --//
        propertyId = intent.getStringExtra(Constants.PROPERTY_ID)!!
        if (propertyId != "") {
            editDataViewModel.getListOfPhotos(propertyId)
            editDataViewModel.listPhotosLiveData.observe(this, Observer {
                it.forEach { photo -> imageList.add(photo) }
            })
            getDataFromDatabase(propertyId)
        }
    }

    /**
     * Get data from the PropertyDatabase if the propertyId is not equals to 0
     * @param id The id of the property
     */
    private fun getDataFromDatabase(id: String) {
        editDataViewModel.getPropertyFromId(id)
        editDataViewModel.propertyLiveData.observe(this, Observer {
            updateViewsWithRoomData(it)
        })
        editDataViewModel.getAddressOfOneProperty(id)
        editDataViewModel.addressLiveData.observe(this, Observer {
            updateAddressWithRoomData(it)
        })
        editDataViewModel.getAllPhotos(id)
        editDataViewModel.photosLiveData.observe(this, Observer {
            photosAdapter.setData(it)
            photosAdapter.notifyDataSetChanged()
        })
    }

    /**
     * When click on the FAB
     */
    override fun onClick(v: View?) {
        when (v) {
            saveBtn -> {
                //-- Save property in database and Firestore --//
                if (checkRequiredInfo()) {
                    val property = Property(propertyId, getCurrentUser().uid, spinnerType.selectedItem.toString().toUpperCase(Locale.ROOT),
                            price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(),
                            numberBedrooms.text.toString().toInt(), numberBathrooms.text.toString().toInt(), description.text.toString(),
                            inSaleRadioBtn.isChecked, editDataViewModel.getNearby(nearbyNachos.chipValues), inSaleDate.text.toString(), soldDate.text.toString(), parseLocalDateTimeToString(LocalDateTime.now()))

                    editDataViewModel.save(property.id_property, property, number.text.toString().toInt(),
                            street.text.toString(), zipCode.text.toString(), city.text.toString(),
                            country.text.toString())
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    priceContainer.error = getString(R.string.cant_be_empty)
                    custom_surface_container.error = getString(R.string.cant_be_empty)
                    custom_rooms_container.error = getString(R.string.cant_be_empty)
                    address_country_container.error = getString(R.string.cant_be_empty)
                    custom_type_container.error = getString(R.string.cant_be_empty)
                }
            }
            addPhotoImg -> {
                //-- Ask permission and let user select an image from his phone --//
                if (!EasyPermissions.hasPermissions(this, Constants.PERMS)) {
                    EasyPermissions.requestPermissions(this, "Permission", Constants.RC_PERMISSION_FILES_STORAGE, Constants.PERMS)
                    return
                }
                selectAnImageFromThePhone()
            }
            soldRadioBtn -> {
                soldRadioBtn.isChecked = true
                inSaleRadioBtn.isChecked = false
            }
            inSaleRadioBtn -> {
                inSaleRadioBtn.isChecked = true
                soldRadioBtn.isChecked = false
            }
            addPhotoBtn -> addImageToListAndShowIt()
            inSaleDate -> getDateFromDatePicker(inSaleDate)
            soldDate -> getDateFromDatePicker(soldDate)
        }
    }

    private fun checkRequiredInfo(): Boolean {
        return price.text.toString() != "" && surface.text.toString() != "" && rooms.text.toString() != "" && country.text.toString() != "" && spinnerType.selectedItem != null
    }

    private fun getDateFromDatePicker(view: TextView) {
        val calendar = Calendar.getInstance()
        val actualYear = calendar.get(Calendar.YEAR)
        val actualMonth = calendar.get(Calendar.MONTH)
        val actualDay = calendar.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    view.text = getString(R.string.details_date, day.toString(), month.toString(), year.toString())
                },
                actualYear, actualMonth, actualDay)
        picker.show()
    }

    //-- MANAGE IMAGE --//
    private fun selectAnImageFromThePhone() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, Constants.RC_CHOOSE_IMAGE)
    }

    private fun addImageToListAndShowIt() {
        desImage = addPhotoTxt.text.toString()
        if (uriImage != null && desImage != "") {
            val photo = Photo(uriImage.toString(), desImage, false)
            imageList.add(photo)
            photosAdapter.setData(imageList)
            photosAdapter.notifyDataSetChanged()
        } else if (desImage == "") {
            Snackbar.make(layout, getString(R.string.description_required), Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(layout, getString(R.string.image_and_description_required), Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadImage(requestCode, resultCode, data)
    }

    private fun loadImage(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.RC_CHOOSE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uriImage = data!!.data!!
                Glide.with(this).load(uriImage).into(addPhotoImg)
                addPhotoBtn.visibility = View.VISIBLE
            } else {
                Snackbar.make(layout, "Error with image", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    //-- CONFIGURATION --//
    private fun configureViewModel() {
        val editDataViewModelFactory = Injection.provideViewModelFactory(this)
        editDataViewModel = ViewModelProviders.of(this, editDataViewModelFactory).get(EditDataViewModel::class.java)
    }

    /**
     * Set adapter for TypeSpinner, AgentSpinner and NearbyNachos
     */
    private fun setSpinnerAndNachosAdapters() {
        val adapterType = TypeEnumSpinnerAdapter(this, editDataViewModel.getTypesEnumList())
        spinnerType.adapter = adapterType
        val adapter = NearbyAdapter(this, getNearbyList())
        nearbyNachos.setAdapter(adapter)
        editDataViewModel.getAllUser()
        editDataViewModel.userListLiveData.observe(this, Observer {
            val adapterAgent = AgentSpinnerAdapter(this, it)
            spinnerAgent.adapter = adapterAgent
        })
    }

    /**
     * Bind all views
     */
    private fun bindViews() {
        spinnerType = findViewById(R.id.add_edit_type_spinner)
        spinnerAgent = findViewById(R.id.agent_spinner)
        nearbyNachos = findViewById(R.id.edit_nearby)
        price = findViewById(R.id.edit_price)
        surface = findViewById(R.id.edit_surface)
        rooms = findViewById(R.id.edit_rooms)
        description = findViewById(R.id.edit_description)
        numberBathrooms = findViewById(R.id.edit_bathrooms)
        numberBedrooms = findViewById(R.id.edit_bedrooms)
        layout = findViewById(R.id.edit_container)
        saveBtn = findViewById(R.id.edit_save_btn)
        saveBtn.setOnClickListener(this)
        number = findViewById(R.id.edit_address_number)
        street = findViewById(R.id.edit_address_street)
        zipCode = findViewById(R.id.edit_address_zip_code)
        city = findViewById(R.id.edit_address_city)
        country = findViewById(R.id.edit_address_country)
        addPhotoImg = findViewById(R.id.edit_add_photos)
        addPhotoImg.setOnClickListener(this)
        addPhotoTxt = findViewById(R.id.edit_add_photos_txt)
        addPhotoBtn = findViewById(R.id.edit_add_photos_btn)
        addPhotoBtn.setOnClickListener(this)
        recyclerView = findViewById(R.id.edit_list_photos)
        deletePhotoBtn = findViewById(R.id.edit_delete_btn)
        inSaleRadioBtn = findViewById(R.id.edit_on_sale_btn)
        inSaleRadioBtn.setOnClickListener(this)
        soldRadioBtn = findViewById(R.id.edit_sold_btn)
        soldRadioBtn.setOnClickListener(this)
        inSaleDate = findViewById(R.id.edit_date_created)
        inSaleDate.setOnClickListener(this)
        soldDate = findViewById(R.id.edit_date_sold)
        soldDate.setOnClickListener(this)
        priceContainer = findViewById(R.id.custom_price_container)

    }

    //-- SAVE INSTANCE --//
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(Constants.KEY_TEXT_PRICE, price.text)
        outState.putCharSequence(Constants.KEY_TEXT_SURFACE, surface.text)
        outState.putCharSequence(Constants.KEY_TEXT_ROOMS, rooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_DESCRIPTION, description.text)
        outState.putCharSequence(Constants.KEY_TEXT_BEDROOMS, numberBedrooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_BATHROOMS, numberBathrooms.text)
        outState.putCharSequence(Constants.KEY_TEXT_TYPE, spinnerType.selectedItem.toString())
    }

    /**
     * Get the SaveInstance in case of the screen's orientation change
     */
    private fun getSaveInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            setText(price, Constants.KEY_TEXT_PRICE, savedInstanceState)
            setText(surface, Constants.KEY_TEXT_SURFACE, savedInstanceState)
            setText(rooms, Constants.KEY_TEXT_ROOMS, savedInstanceState)
            setText(description, Constants.KEY_TEXT_DESCRIPTION, savedInstanceState)
            setText(numberBedrooms, Constants.KEY_TEXT_BEDROOMS, savedInstanceState)
            setText(numberBathrooms, Constants.KEY_TEXT_BATHROOMS, savedInstanceState)
        }
    }

    private fun setText(view: TextView, key: String, savedInstanceState: Bundle?) {
        val text = savedInstanceState?.getCharSequence(key)
        view.text = text
    }

    //-- UPDATE VIEWS --//
    /**
     * Update the views with the data got from PropertyDatabase
     */
    private fun updateViewsWithRoomData(property: Property) {
        price.setText(property.price.toString())
        surface.setText(property.surface.toString())
        rooms.setText(property.rooms_nbr.toString())
        description.setText(property.description)
        numberBedrooms.setText(property.bed_nbr.toString())
        numberBathrooms.setText(property.bath_nbr.toString())

        val nearby = property.nearby!!.split(",").toString()
        nearbyNachos.addChipTerminator(',', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        Log.d("Nearby", property.nearby.toString())
        nearbyNachos.setText(nearby)

        if (property.in_sale) {
            inSaleRadioBtn.isChecked = true
        } else {
            soldRadioBtn.isChecked = true
        }
        inSaleDate.text = property.created_date
        soldDate.text = property.sold_date
    }

    /**
     * Update address information with the data got from PropertyDatabase
     */
    private fun updateAddressWithRoomData(address: Address) {
        number.setText(address.number.toString())
        street.setText(address.street)
        zipCode.setText(address.zip_code)
        city.setText(address.city)
        country.setText(address.country)
    }

    //-- Request WRITE_EXTERNAL_STORAGE permission --//
    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE), Constants.RC_PERMISSION_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onItemClicked(photo: Photo, position: Int) {
        editDataViewModel.photoClicked(photo.uri!!)
        deletePhotoBtn.show()
        deletePhotoBtn.setOnClickListener {
            editDataViewModel.deletePhotos(propertyId, photo)
            deletePhotoBtn.hide()
        }
    }
}