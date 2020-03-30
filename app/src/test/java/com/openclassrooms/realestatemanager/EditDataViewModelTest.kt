package com.openclassrooms.realestatemanager

import android.content.Context
import android.database.CursorJoiner
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Operation
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.EditDataViewModel
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.search.NearbyEnum
import com.openclassrooms.realestatemanager.search.TypeEnum
import com.openclassrooms.realestatemanager.utils.FakePhotoDataRepository
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
    private lateinit var fakePhotoDataRepository: FakePhotoDataRepository

    @Mock
    private lateinit var fakePropertyDataRepository: FakePropertyDataRepository

    @Mock
    private lateinit var addressDataRepository: AddressDataRepository

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
        viewModel = EditDataViewModel(context, fakePhotoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, userDataRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getAllUsersFromDatabase_contains2() = runBlockingTest {
        val list = listOf(User("566413", "Martin", "martin@orange.fr", "profil.martin/image.fr"), User("35645", "John", "john@free.fr", "profil.john/image.fr"))
        val mockUserDataRepository = mock<UserDataRepository>{
            onBlocking { getAllUsers() } doReturn list
        }
        viewModel = EditDataViewModel(context, fakePhotoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, mockUserDataRepository)

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
        val mockUserDataRepository = mock<UserDataRepository>{
            onBlocking { getAllUsers() } doReturn list
        }
        viewModel = EditDataViewModel(context, fakePhotoDataRepository, fakePropertyDataRepository, addressDataRepository, executor, mockUserDataRepository)

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
        val list = arrayListOf(TypeEnum.HOUSE, TypeEnum.APARTMENT, TypeEnum.DUPLEX, TypeEnum.LOFT)
        val listTest = viewModel.getTypesList()
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
    fun getAllNearbyPlacesFromEnumConstant() {
        val list = listOf(NearbyEnum.BUS, NearbyEnum.SHOP, NearbyEnum.SCHOOL, NearbyEnum.POST, NearbyEnum.PUBLIC_TRANSPORT, NearbyEnum.RESTAURANT, NearbyEnum.UNIVERSITY)
        assertEquals(list, viewModel.getNearbyList())
    }

    @Test
    fun getAddressOfProperty_WithIdEqual22() = runBlockingTest {
        val address = Address("01", 4, "allée des Bleuets", null, "Louhans","France", "22")
        val mockAddressDataRepository = mock<AddressDataRepository> {
            onBlocking { getAddressOfOneProperty("22") } doReturn address
        }
        viewModel = EditDataViewModel(context, fakePhotoDataRepository, fakePropertyDataRepository, mockAddressDataRepository, executor, userDataRepository)

        viewModel.getAddressOfOneProperty("22")

        val addressFound = viewModel.addressLiveData.getOrAwaitValue()
        assertEquals("allée des Bleuets", addressFound.street)
        assertEquals("01", addressFound.id_address)
        assertEquals(null, addressFound.zip_code)
        assertEquals("22", addressFound.idProperty)
    }

    @Test
    fun getPropertyWithId() = runBlockingTest {
        val property = Property("001", "025","House", 250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25")
        val mockPropertyDataRepository = mock<PropertyDataRepository>{
            onBlocking { getPropertyFromId("001") } doReturn property
        }
        viewModel = EditDataViewModel(context, fakePhotoDataRepository, mockPropertyDataRepository, addressDataRepository, executor, userDataRepository)

        viewModel.getPropertyFromId("001")

        val propertyFound = viewModel.propertyLiveData.getOrAwaitValue()
        assertEquals("025", propertyFound.agent)
        assertEquals("House", propertyFound.type)
        assertEquals(250500, propertyFound.price)
        assertEquals(4, propertyFound.rooms_nbr)
    }

    @Test
    fun addPropertyWithSuccessAndGetIt() = runBlockingTest {
        val property = Property("001", "025","House", 250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25")
        val SUCCESS = 2L
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { addProperty(property) } doReturn SUCCESS
            onBlocking { getPropertyFromId("001") } doReturn property
        }
        viewModel = EditDataViewModel(context, fakePhotoDataRepository, mockPropertyDataRepository, addressDataRepository, executor, userDataRepository)

        viewModel.addProperty(property)
        viewModel.getPropertyFromId("001")

        val propertyFound = viewModel.propertyLiveData.getOrAwaitValue()
        assertEquals(125, propertyFound.surface)
        assertEquals("Big house", propertyFound.description)
    }
}