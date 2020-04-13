package com.openclassrooms.realestatemanager

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.show.detail.DetailViewModel
import com.openclassrooms.realestatemanager.show.geocode_model.GeocodeRepository
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
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
class DetailsViewModelTest {

    private lateinit var viewModel: DetailViewModel

    @Mock
    private lateinit var propertyDataRepository: PropertyDataRepository

    @Mock
    private lateinit var geocodeRepository: GeocodeRepository

    @Mock
    private lateinit var addressDataRepository: AddressDataRepository

    @Mock
    private lateinit var photoDataRepository: PhotoDataRepository

    @Mock
    private lateinit var userDataRepository: UserDataRepository

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    @Test
    fun getPropertyFromDatabaseById() = runBlockingTest {
        val property = Property("001", "025", "House", 0,250500, 125, 4, 2, 2, "Big house", true, "RESTAURANT", "12/03/2020", null, "2020-03-12T12:20:25")
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getPropertyFromId("001") } doReturn property
        }
        viewModel = DetailViewModel(mockPropertyDataRepository, geocodeRepository, addressDataRepository, photoDataRepository, userDataRepository)
        viewModel.getPropertyFromId("001")

        val propertyFound = viewModel.propertyLiveData.getOrAwaitValue()
        assertEquals("025", propertyFound.agent)
        assertEquals(true, propertyFound.in_sale)
        assertEquals("RESTAURANT", propertyFound.nearby)
    }

    @Test
    fun getAddressOfPropertyById() = runBlockingTest {
        val address = Address("005", 65, "route de Bourgogne", "03640", "GrandVille", "France", "002")
        val mockAddressDataRepository = mock<AddressDataRepository>{
            onBlocking { getAddressOfOneProperty("002") } doReturn address
        }
        viewModel = DetailViewModel(propertyDataRepository, geocodeRepository, mockAddressDataRepository, photoDataRepository, userDataRepository)
        viewModel.getAddressOfOneProperty("002")

        val addressFound = viewModel.addressLiveData.getOrAwaitValue()
        assertEquals("route de Bourgogne", addressFound.street)
        assertEquals("03640", addressFound.zip_code)
    }

    @Test
    fun getPhotos_ContainsOne()= runBlockingTest {
        val listPhoto = arrayListOf(Photo("salon/image.fr", "salon", false))
        val mockPhotoDataRepository = mock<PhotoDataRepository> {
            onBlocking { getListOfPhotos("001") } doReturn listPhoto
        }
        viewModel = DetailViewModel(propertyDataRepository, geocodeRepository, addressDataRepository, mockPhotoDataRepository, userDataRepository)
        viewModel.getListOfPhotos("001")

        val listFound = viewModel.listPhotosLiveData.getOrAwaitValue()
        assertEquals(1, listFound.size)
    }


    @Test
    fun getAgentManagingTheProperty() = runBlockingTest {
        val agent = User("025", "Alice", "alice@orange.fr", null)
        val mockUserDataRepository = mock<UserDataRepository>{
            onBlocking { getUserById("025") } doReturn agent
        }
        viewModel = DetailViewModel(propertyDataRepository, geocodeRepository, addressDataRepository, photoDataRepository, mockUserDataRepository)
        viewModel.setAgent("025")

        val agentFound = viewModel.agentLiveData.getOrAwaitValue()
        assertEquals("Alice", agentFound.name)
        assertEquals(null, agentFound.photo)
    }


}