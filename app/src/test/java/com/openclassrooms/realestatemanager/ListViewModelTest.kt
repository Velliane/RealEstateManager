package com.openclassrooms.realestatemanager

import android.content.Context
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.show.list.ListViewModel
import com.openclassrooms.realestatemanager.utils.FakeAddressDataRepository
import com.openclassrooms.realestatemanager.utils.FakePhotoDataRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class ListViewModelTest{

    private lateinit var viewModel: ListViewModel

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var fakeAddressDataRepository: FakeAddressDataRepository
    @Mock
    private lateinit var fakePhotoDataRepository: FakePhotoDataRepository

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() = runBlocking {
        MockitoAnnotations.initMocks(this)
        doReturn("android.resource://com.openclassrooms.realestatemanager/drawable/no_image_available_64").`when`(context).getString(any(Int::class.javaObjectType))
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
        viewModel = ListViewModel(context, mockPropertyDataRepository, fakeAddressDataRepository, fakePhotoDataRepository)
    }

    @Test
    fun setResetButtonToVisibilityVisible() {
        viewModel.setButtonVisibility(true)
        assertEquals(View.VISIBLE, viewModel.resetBtnLiveData.value)
    }

    @Test
    fun setResetButtonToVisibilityGone() {
        viewModel.setButtonVisibility(false)
        assertEquals(View.GONE, viewModel.resetBtnLiveData.value)
    }

    @Test
    fun getIdOfPorpertySelected() {
        viewModel.propertyClicked("001")
        assertEquals("001", viewModel.currentIdPropertySelectedLiveData.value)
    }

    @Test
    fun getAllPropertyWithTheirAddress() {
        viewModel.propertyClicked("001")
        viewModel.propertiesLiveData.observeForever(Observer {
            assertEquals(2, it.size)
        })
    }

    @Test
    fun getListOfPhoto() {
        val photo = viewModel.getPhotoForPropertyId("001")
        assertEquals("Salon séjour avec cheminée", photo.description)
    }

}