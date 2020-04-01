package com.openclassrooms.realestatemanager

import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.show.map.MapViewModel
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MapViewModelTest {

    private lateinit var viewModel: MapViewModel

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var addressDataRepository: AddressDataRepository
    @Mock
    private lateinit var geocodeRepository: GeocodeRepository
    private val query = SimpleSQLiteQuery("SELECT * FROM Property WHERE price = 250500")
    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = runBlockingTest {
        MockitoAnnotations.initMocks(this)
        val listPropertiesLiveData = MutableLiveData<List<Property>>()
        val list = arrayListOf(Property("001", "025","House", 250500, 125, 4, 2, 2, "Big house", true, "BUS, RESTAURANT", "03/02/2020", null, "2020-03-12T12:20:25"), Property("002", "025","House", 185000, 75, 3, 1, 2, "Little house", true, "RESTAURANT", "23/03/2020", "30/03/2020","2020-03-09T12:20:25"))
        listPropertiesLiveData.value = list
        //-- List for research --//
        val listSearchedLiveData = MutableLiveData<List<Property>>()
        val listSearched = arrayListOf(Property("001", "025","House", 250500, 125, 4, 2, 2, "Big house", true, "BUS, RESTAURANT", "03/02/2020", null, "2020-03-12T12:20:25"))
        listSearchedLiveData.value = listSearched
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn listPropertiesLiveData
            onBlocking { searchInDatabase(query) } doReturn listSearchedLiveData
        }
        val address1 = Address("01", 4, "allée des Bleuets", null, "Louhans","France", "001")
        val address2 = Address("05", 45, "rue Victor Hugo", "69100", "Villeurbanne","France", "002")
        val mockAddressDataRepository = mock<AddressDataRepository> {
            onBlocking { getAddressOfOneProperty("001") } doReturn address1
            onBlocking { getAddressOfOneProperty("002") } doReturn address2
        }
        viewModel = MapViewModel(mockPropertyDataRepository, mockAddressDataRepository, geocodeRepository)
    }

    @Test
    fun getAllPropertyWithTheirAddresses_ListContains2Properties() {
        val listOfPropertiesFound = viewModel.propertiesLiveData.getOrAwaitValue()
        val propertyOne = listOfPropertiesFound[0]
        assertEquals(2, listOfPropertiesFound.size)
        assertEquals(250500, propertyOne.price.toInt())
        assertTrue(listOfPropertiesFound.isNotEmpty())
    }

    @Test
    fun turnStringValueToSimpleSQLiteQuery() {
        val simpleSQLiteQuery = viewModel.stringToSimpleSQLiteQuery("Allée des Lilas")
        assertEquals("Allée des Lilas", simpleSQLiteQuery.sql)
    }

    @Test
    fun getAllPropertyWithTheirAddress_ListNull() {
        val liveData = MutableLiveData<List<Property>>()
        val list = ArrayList<Property>()
        liveData.value = list
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }
        viewModel = MapViewModel(mockPropertyDataRepository, addressDataRepository, geocodeRepository)

        val listOfPropertiesFound = viewModel.propertiesLiveData.getOrAwaitValue()
        assertTrue(listOfPropertiesFound.isEmpty())
    }

    @Test
    fun searchProperties_Found1Property() {
        viewModel.searchInDatabase("SELECT * FROM Property WHERE price = 250500")
        val listFound = viewModel.propertiesFromResearchLiveData?.getOrAwaitValue()
        assertTrue(listFound!!.isNotEmpty())
        assertEquals("House", listFound[0].type)
    }

    @Test
    fun setResetButtonToVisibilityVisible() {
        viewModel.setButtonVisibility(true)
        val result = viewModel.resetBtnLiveData.getOrAwaitValue()
        assertEquals(View.VISIBLE, result)
    }

    @Test
    fun setResetButtonToVisibilityGone() {
        viewModel.setButtonVisibility(false)
        val result = viewModel.resetBtnLiveData.getOrAwaitValue()
        assertEquals(View.GONE, result)
    }
}