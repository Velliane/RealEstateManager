package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.show.list.ListViewModel
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.powermock.core.classloader.annotations.PrepareForTest


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
@PrepareForTest(Uri::class)
class ListViewModelTest{

    private lateinit var viewModel: ListViewModel

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var addressDataRepository: AddressDataRepository

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = runBlockingTest {
        MockitoAnnotations.initMocks(this)
        doReturn("android.resource://com.openclassrooms.realestatemanager/drawable/no_image_available_64").`when`(context).getString(any(Int::class.javaObjectType))
        val liveData = MutableLiveData<List<Property>>()
        val list = ArrayList<Property>()
        //-- Create Properties and add them to list --//
        val property1 = Property("001", "025","House", 0,250500, 125, 4, 2, 2, "Big house", true, "RESTAURANT", "12/03/2020", null, "2020-03-12T12:20:25")
        val property2 = Property("002", "025","House", 0,185000, 75, 3, 1, 2, "Little house", false, null, "12/03/2020", "02/04/2020", "2020-04-02T15:24:35")
        list.add(property1)
        list.add(property2)
        liveData.value = list
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }
        val listPhotos = arrayListOf(Photo("025/image.fr", "Salon séjour avec cheminée", false), Photo("054/imagefr", "Grande chambre", false))
        val mockPhotoDataRepository = mock<PhotoDataRepository> {
            onBlocking { getListOfPhotos("001") } doReturn listPhotos
        }
        viewModel = ListViewModel(context, mockPropertyDataRepository, addressDataRepository, mockPhotoDataRepository)
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

    @Test
    fun getIdOfPorpertySelected() {
        viewModel.propertyClicked("001")
        val result = viewModel.currentIdPropertySelectedLiveData.getOrAwaitValue()
        assertEquals("001", result)
    }

    @Test
    fun getAllPropertyWithTheirAddress() {
        viewModel.propertyClicked("001")
        viewModel.propertiesLiveData.observeForever(Observer {
            assertEquals(2, it.size)
        })
    }

    @Test
    fun getFirstPhotoOfListToShowIt()= runBlockingTest {
        val photo = viewModel.getPhotoForPropertyId("001")
        assertEquals("Salon séjour avec cheminée", photo.description)
        assertEquals("025/image.fr", photo.uri)
    }

    @Test
    fun turnStringValueToSimpleSQLiteQuery() {
        val simpleSQLiteQuery = viewModel.stringToSimpleSQLiteQuery("Allée des Lilas")
        assertEquals("Allée des Lilas", simpleSQLiteQuery.sql)
    }

}