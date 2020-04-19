package com.openclassrooms.realestatemanager.add_edit

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.chip.ChipInfo
import com.jakewharton.threetenabp.AndroidThreeTen
import com.openclassrooms.realestatemanager.BaseActivity
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.search.NearbyEnum
import com.openclassrooms.realestatemanager.search.TypeEnum
import com.openclassrooms.realestatemanager.show.MainActivity
import com.openclassrooms.realestatemanager.show.detail.PhotosAdapter
import com.openclassrooms.realestatemanager.utils.*
import kotlinx.android.synthetic.main.activity_edit_add.*
import kotlinx.android.synthetic.main.custom_address.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import pub.devrel.easypermissions.EasyPermissions
import java.io.*
import java.text.SimpleDateFormat
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
    private lateinit var chipGroup: ChipGroup
    /** Adapter for TypeSpinner */
    private lateinit var adapterType: TypeEnumSpinnerAdapter
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
    /** Deleted photos list */
    private val deletedPhotos = ArrayList<Photo>()
    /** Address*/
    private var address: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(this)
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
            getDataFromDatabase(propertyId)
        }else{
            inSaleDate.text = Utils.getTodayDate(LocalDate.now())
            inSaleRadioBtn.isChecked = true
        }
    }

    /**
     * Get data from the PropertyDatabase if the propertyId is not equals to 0
     * @param id The id of the property
     */
    private fun getDataFromDatabase(id: String) {
        editDataViewModel.getPropertyToEdit(id)
        editDataViewModel.propertyToEditLiveData.observe(this, Observer {
            updateViewsWithRoomData(it)
            address = it.address
        })
        editDataViewModel.getAllPhotos(id)
        editDataViewModel.photosLiveData.observe(this, Observer {
            it.forEach { photo -> imageList.add(photo) }
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
                //-- Check required info --//
                if (checkRequiredInfo()) {
                    val type = spinnerType.selectedItem.toString().toUpperCase(Locale.ROOT)
                    var agent = ""
                    editDataViewModel.userListLiveData.observe(this, Observer {
                        agent = it[spinnerAgent.selectedItemPosition].userId
                    })
                    //-- Create property --//
                    val property = Property(propertyId, agent, type, TypeEnum.valueOf(type).ordinal, price.text.toString().toInt(), surface.text.toString().toInt(), rooms.text.toString().toInt(),
                            numberBedrooms.text.toString().toInt(), numberBathrooms.text.toString().toInt(), description.text.toString(), inSaleRadioBtn.isChecked, editDataViewModel.getNearby(nearbyNachos.chipValues),
                            inSaleDate.text.toString(), soldDate.text.toString(), parseLocalDateTimeToString(LocalDateTime.now()))
                    //-- Save it --//
                    editDataViewModel.save(propertyId, property, number.text.toString().toInt(),
                            street.text.toString(), zipCode.text.toString(), city.text.toString(),
                            country.text.toString(), address)
                    //-- Return to MainActivity --//
                    if(editDataViewModel.savedSuccessLiveData.value == true){
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                } else {
                    //-- Show error message if required info are not found --//
                    if (imageList.isEmpty()) {
                        Snackbar.make(layout, getString(R.string.add_edit_error_select_one_image), Snackbar.LENGTH_SHORT).show()
                    }
                    priceContainer.error = getString(R.string.cant_be_empty)
                    custom_surface_container.error = getString(R.string.cant_be_empty)
                    custom_rooms_container.error = getString(R.string.cant_be_empty)
                    address_country_container.error = getString(R.string.cant_be_empty)
                }
            }
            addPhotoImg -> {
                //-- Ask permission and let user select an image from his phone --//
                if (!EasyPermissions.hasPermissions(this, Constants.PERMS)) {
                    EasyPermissions.requestPermissions(this, "Permission", Constants.RC_PERMISSION_FILES_STORAGE, Constants.PERMS)
                    return
                }
                openphotoOriginDialog()
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
        return price.text.toString() != "" && surface.text.toString() != "" && rooms.text.toString() != "" && country.text.toString() != "" && spinnerType.selectedItem != null && imageList.isNotEmpty()
    }

    /**
     * Open DatePickerDialog to let user choose a date for
     * createdDate and soldDate
     */
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
    /**
     * Open an AlertDialog to ask user if he want to add photo from Gallery or from Camera
     */
    private fun openphotoOriginDialog() {
        val options = arrayOf<CharSequence>(getString(R.string.add_photo_from_camera), getString(R.string.add_photo_from_gallery), getString(R.string.add_photo_cancel))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_photo_dialog_title))
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == getString(R.string.add_photo_from_camera) -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, Constants.RC_CAMERA)
                }
                options[item] == getString(R.string.add_photo_from_gallery) -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, Constants.RC_CHOOSE_IMAGE)
                }
                options[item] == getString(R.string.add_photo_cancel) -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    /**
     * Check if an image is choose and a description is written
     * If true, add Photo to list and show it in Recycler View
     * Else, show SnackBar with error message
     */
    private fun addImageToListAndShowIt() {
        desImage = addPhotoTxt.text.toString()
        if (uriImage != null && desImage != "") {
            val photo = Photo(uriImage.toString(), desImage, false)
            editDataViewModel.addPhotoToList(photo)
            imageList.add(photo)
            photosAdapter.setData(imageList)
            photosAdapter.notifyDataSetChanged()
            Glide.with(this).load(R.drawable.action_add).into(addPhotoImg)
            addPhotoTxt.setText("")
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

    /**
     * Get image choose by user and show it in preview with Glide
     */
    private fun loadImage(requestCode: Int, resultCode: Int, data: Intent?) {
        //-- Get image from phone's gallery --//
        assert(data != null)
        if (requestCode == Constants.RC_CHOOSE_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uriImage = data?.data
                Glide.with(this).load(uriImage).into(addPhotoImg)
                addPhotoBtn.visibility = View.VISIBLE
            } else {
                Snackbar.make(layout, getString(R.string.error_loading_img), Snackbar.LENGTH_SHORT).show()
            }
            //-- Let user take a photo and save it in folder 'RealEstateManager' in SD card --//
        } else if (requestCode == Constants.RC_CAMERA) {
            val bitmap = data?.extras!!["data"] as Bitmap?
            val bytes = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, bytes)

            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val destination = File(Environment.getExternalStorageDirectory().path + "/RealEstateManager/", "IMG_$timeStamp.jpg")
            val fo: FileOutputStream
            try {
                destination.createNewFile()
                fo = FileOutputStream(destination)
                fo.write(bytes.toByteArray())
                fo.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val path = destination.absolutePath
            val file = File(path)
            uriImage = Uri.fromFile(file)
            Glide.with(this).load(uriImage).into(addPhotoImg)
        }
        if(data == null){
            Snackbar.make(layout, getString(R.string.error_loading_img), Snackbar.LENGTH_SHORT).show()
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
        adapterType = TypeEnumSpinnerAdapter(this, editDataViewModel.getTypesEnumList())
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
        chipGroup = findViewById(R.id.edit_show_chip_group)
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
        outState.putCharSequence(Constants.KEY_TEXT_AGENT, spinnerAgent.selectedItem.toString())
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
            spinnerType.setSelection((savedInstanceState.getCharSequence(Constants.KEY_TEXT_TYPE)).toString().toInt())
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
    private fun updateViewsWithRoomData(property: PropertyToEdit) {
        price.setText(property.price.toString())
        surface.setText(property.surface.toString())
        rooms.setText(property.rooms_nbr.toString())
        description.setText(property.description)
        numberBedrooms.setText(property.bed_nbr.toString())
        numberBathrooms.setText(property.bath_nbr.toString())
        val typeEnum = TypeEnum.values()[property.type_id]
        spinnerType.setSelection(adapterType.getPosition(typeEnum))
        if (property.nearby != "") {
            val nearby = property.nearby!!.split(",").toTypedArray()
            val listOfChip = ArrayList<ChipInfo>()
            for (item in nearby) {
                val chip = ChipInfo(getString(NearbyEnum.valueOf(item).res), null)
                listOfChip.add(chip)
            }
            nearbyNachos.setTextWithChips(listOfChip)
        }
        if (property.in_sale) {
            inSaleRadioBtn.isChecked = true
        } else {
            soldRadioBtn.isChecked = true
        }
        inSaleDate.text = property.created_date
        soldDate.text = property.sold_date

        updateAddressWithRoomData(property.address)
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

    /**
     * When click on a photo, set it selected
     * User can delete selected photo
     */
    override fun onItemClicked(photo: Photo, position: Int) {
        editDataViewModel.photoClicked(photo.uri!!)
        deletePhotoBtn.show()
        deletePhotoBtn.setOnClickListener {
            editDataViewModel.deletePhotos(propertyId, photo)
            deletedPhotos.add(photo)
            deletePhotoBtn.hide()
        }
    }

    override fun onBackPressed() {
        //-- If edited is canceled, restore deleted photos --//
        if(propertyId != ""){
            for(photo in deletedPhotos){
                editDataViewModel.addPhotoToList(photo)
            }
            editDataViewModel.savePhotos(propertyId)
        }
        super.onBackPressed()
    }
}