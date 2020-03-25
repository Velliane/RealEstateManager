package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.show.map.MapViewModel
import com.openclassrooms.realestatemanager.utils.FakeAddressDataRepository
import com.openclassrooms.realestatemanager.utils.FakeFirestoreDataRepository
import com.openclassrooms.realestatemanager.utils.FakePropertyDataRepository
import com.openclassrooms.realestatemanager.utils.FakeUserDataRepository
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


@RunWith(JUnit4::class)
class MapViewModelTest {

    private lateinit var viewModel: MapViewModel

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var fakePropertyDataRepository: FakePropertyDataRepository
    @Mock
    private lateinit var fakeAddressDataRepository: FakeAddressDataRepository
    @Mock
    private lateinit var geocodeRepository: GeocodeRepository
    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        val liveData = MutableLiveData<List<Property>>()
        val list = ArrayList<Property>()
        //-- Create Properties and add them to list --//
        val property1 = Property("001", "025","House", 250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25")
        val property2 = Property("002", "025","House", 185000, 75, 3, 1, 2, "Little house", true, "2020-03-09T12:20:25")
        list.add(property1)
        list.add(property2)
        liveData.value = list

        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }
        viewModel = MapViewModel(mockPropertyDataRepository, fakeAddressDataRepository, geocodeRepository)
    }

    @Test
    fun setStringToSimpleSQLiteQuery() {
        val query = viewModel.stringToSimpleSQLiteQuery("4 rue des lilas")
        assertEquals("4 rue des lilas", query)
    }

}