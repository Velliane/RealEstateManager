package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.EditDataViewModel
import com.openclassrooms.realestatemanager.add_edit.Photo
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PhotoDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.data.UserDataRepository
import com.openclassrooms.realestatemanager.search.TypeEnum
import com.openclassrooms.realestatemanager.utils.FakePropertyDataRepository
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import junit.framework.Assert.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.concurrent.Executor

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class EditDataViewModelTest {


    private lateinit var viewModel: EditDataViewModel

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var photoDataRepository: PhotoDataRepository

    @Mock
    private lateinit var fakePropertyDataRepository: FakePropertyDataRepository

    @Mock
    private lateinit var addressDataRepository: AddressDataRepository

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var executor: Executor

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
        viewModel = EditDataViewModel(firebaseAuth, context, photoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, userDataRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getAllUsersFromDatabase_contains2() = runBlockingTest {
        val list = listOf(User("566413", "Martin", "martin@orange.fr", "profil.martin/image.fr"), User("35645", "John", "john@free.fr", "profil.john/image.fr"))
        val mockUserDataRepository = mock<UserDataRepository> {
            onBlocking { getAllUsers() } doReturn list
        }
        viewModel = EditDataViewModel(firebaseAuth, context, photoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, mockUserDataRepository)

        viewModel.getAllUser()
        val listResult = viewModel.userListLiveData.getOrAwaitValue()
        assertEquals(2, listResult.size)
        assertEquals("Martin", listResult[0].name)
        assertEquals("35645", listResult[1].userId)
        assertNotSame("652345", listResult[1].userId)
    }

    @Test
    fun getAllUsersFromDatabase_contains5() = runBlockingTest {
        val list = listOf(User("566413", "Martin", "martin@orange.fr", "profil.martin/image.fr"),
                User("35645", "John", "john@free.fr", "profil.john/image.fr"),
                User("6452", "Eric", "eric@mail.com", null),
                User("365454", "Mélanie", "mel@orange.fr", "hello.png"),
                User("0025464", "Steven", "st.ven@free.fr", "steven.image.com"))
        val mockUserDataRepository = mock<UserDataRepository> {
            onBlocking { getAllUsers() } doReturn list
        }
        viewModel = EditDataViewModel(firebaseAuth, context, photoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, mockUserDataRepository)

        viewModel.getAllUser()
        val listResult = viewModel.userListLiveData.getOrAwaitValue()
        assertEquals(5, listResult.size)
        assertEquals("Steven", listResult[4].name)
        assertEquals(null, listResult[2].photo)
        assertEquals("eric@mail.com", listResult[2].email)
        assertNotSame("652345", listResult[4].userId)
    }

    @Test
    fun getTypesEnumList() {
        val list = arrayListOf(TypeEnum.HOUSE, TypeEnum.APARTMENT, TypeEnum.DUPLEX, TypeEnum.LOFT, TypeEnum.VILLA)
        val listTest = viewModel.getTypesEnumList()
        assertEquals(list, listTest)
    }

    @Test
    fun setListOfNearbyToString() {
        val list = listOf("BUS", "SCHOOL", "RESTAURANT")
        assertEquals("BUS,SCHOOL,RESTAURANT", viewModel.getNearby(list))

        val list2 = listOf("SCHOOL")
        assertEquals("SCHOOL", viewModel.getNearby(list2))
    }


    @Test
    fun getAddressOfProperty_WithIdEqual22() = runBlockingTest {
        val address = Address("01", 4, "allée des Bleuets", null, "Louhans", "France", "22")
        val mockAddressDataRepository = mock<AddressDataRepository> {
            onBlocking { getAddressOfOneProperty("22") } doReturn address
        }
        viewModel = EditDataViewModel(firebaseAuth, context, photoDataRepository, fakePropertyDataRepository, mockAddressDataRepository, executor, userDataRepository)

        viewModel.getAddressOfOneProperty("22")

        val addressFound = viewModel.addressLiveData.getOrAwaitValue()
        assertEquals("allée des Bleuets", addressFound.street)
        assertEquals("01", addressFound.id_address)
        assertEquals(null, addressFound.zip_code)
        assertEquals("22", addressFound.idProperty)
    }

    @Test
    fun getPropertyWithId() = runBlockingTest {
        val property = Property("001", "025", "House", 0, 250500, 125, 4, 2, 2, "Big house", true, "RESTAURANT", "12/03/2020", null, "2020-03-12T12:20:25")
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getPropertyFromId("001") } doReturn property
        }
        viewModel = EditDataViewModel(firebaseAuth, context, photoDataRepository, mockPropertyDataRepository, addressDataRepository, executor, userDataRepository)

        viewModel.getPropertyFromId("001")

        val propertyFound = viewModel.propertyLiveData.getOrAwaitValue()
        assertEquals("025", propertyFound.agent)
        assertEquals("House", propertyFound.type)
        assertEquals(250500, propertyFound.price)
        assertEquals(4, propertyFound.rooms_nbr)
    }

    @Test
    fun addPropertyWithSuccessAndGetIt() = runBlockingTest {
        val property = Property("001", "025", "House", 0, 250500, 125, 4, 2, 2, "Big house", true, "RESTAURANT", "12/03/2020", null, "2020-03-12T12:20:25")
        val SUCCESS = 2L
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { addProperty(property) } doReturn SUCCESS
            onBlocking { getPropertyFromId("001") } doReturn property
        }
        viewModel = EditDataViewModel(firebaseAuth, context, photoDataRepository, mockPropertyDataRepository, addressDataRepository, executor, userDataRepository)

        viewModel.addProperty(property)
        viewModel.getPropertyFromId("001")

        val propertyFound = viewModel.propertyLiveData.getOrAwaitValue()
        assertEquals(125, propertyFound.surface)
        assertEquals("Big house", propertyFound.description)
    }

    @Test
    fun getListOfPhotosFromExternalStorage_ContainsThreePhotos() {
        val listOfPhotos = arrayListOf(Photo("sd/65455.png", "Chambre", false),
                Photo("sd/97844.png", "Salon", false), Photo("sd/95132.png", "Salle de bains", false))
        val mockPhotoDataRepository = mock<PhotoDataRepository> {
            onBlocking { getListOfPhotos("002") } doReturn listOfPhotos
        }
        viewModel = EditDataViewModel(firebaseAuth, context, mockPhotoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, userDataRepository)

        viewModel.getListOfPhotos("002")

        val listFound = viewModel.listPhotosLiveData.getOrAwaitValue()
        assertFalse(listFound.isEmpty())
        assertEquals(3, listFound.size)
    }

    @Test
    fun getListOfAllPhotos_ContainsTwo_WithOnSelected() {
        val listOfPhotos = arrayListOf(Photo("sd/65455.png", "Chambre", false),
                Photo("sd/97844.png", "Salon", false))
        val mockPhotoDataRepository = mock<PhotoDataRepository> {
            onBlocking { getListOfPhotos("002") } doReturn listOfPhotos
        }
        viewModel = EditDataViewModel(firebaseAuth, context, mockPhotoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, userDataRepository)
        viewModel.photoClicked("sd/97844.png")

        viewModel.getAllPhotos("002")
        val list = viewModel.photosLiveData.getOrAwaitValue()
        assertEquals(2, list.size)
        assertEquals("sd/97844.png", viewModel.currentIdPropertySelectedLiveData.getOrAwaitValue())
    }

    @Test
    fun getEditedProperty() {
        val property = Property("001", "025", "House", 0, 250500, 125, 4, 2, 2, "Big house", true, "RESTAURANT", "12/03/2020", null, "2020-03-12T12:20:25")
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getPropertyFromId("001") } doReturn property
        }
        val address = Address("01", 4, "allée des Bleuets", null, "Louhans", "France", "001")
        val mockAddressDataRepository = mock<AddressDataRepository> {
            onBlocking { getAddressOfOneProperty("001") } doReturn address
        }
        viewModel = EditDataViewModel(firebaseAuth, context, photoDataRepository, mockPropertyDataRepository, mockAddressDataRepository, executor, userDataRepository)

        viewModel.getPropertyToEdit("001")
        val propertyEdited = viewModel.propertyToEditLiveData.getOrAwaitValue()
        assertEquals(address, propertyEdited.address)
        assertEquals("025", propertyEdited.agent)
    }

    @Test
    fun whenUserClickOnAPhoto(){
        viewModel.photoClicked("sd/realestatemanager/salon.png")
        val uri = viewModel.currentIdPropertySelectedLiveData.getOrAwaitValue()
        assertEquals("sd/realestatemanager/salon.png", uri)
    }

    @Test
    fun deleteOnePhoto(){
        val listOfPhotos = arrayListOf(Photo("sd/65455.png", "Chambre", false))
        val mockPhotoDataRepository = mock<PhotoDataRepository> {
            onBlocking { getListOfPhotos("002") } doReturn listOfPhotos
        }
        viewModel = EditDataViewModel(firebaseAuth, context, mockPhotoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, userDataRepository)

        viewModel.listPhotosLiveData.postValue(arrayListOf(Photo("sd/65455.png", "Chambre", false),
                Photo("sd/97844.png", "Salon", false)))
        viewModel.deletePhotos("002", Photo("sd/97844.png", "Salon", false))

        viewModel.getListOfPhotos("002")
        val list = viewModel.listPhotosLiveData.getOrAwaitValue()
        assertEquals(1, list.size)
    }

    @Test
    fun addOnePhoto(){
        viewModel.listPhotosLiveData.postValue(arrayListOf(Photo("sd/65455.png", "Chambre", false),
                Photo("sd/97844.png", "Salon", false)))
        viewModel.addPhotoToList(Photo("sd/6fgg55.png", "Cuisine", true))

        val list = viewModel.listPhotosLiveData.getOrAwaitValue()
        assertEquals(3, list.size)
    }
}