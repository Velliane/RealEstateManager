package com.openclassrooms.realestatemanager

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.FirestoreDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.show.MainViewModel
import com.openclassrooms.realestatemanager.show.geocode_model.*
import com.openclassrooms.realestatemanager.utils.FakeFirestoreDataRepository
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import com.openclassrooms.realestatemanager.utils.setAddressToString
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
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var addressDataRepository: AddressDataRepository
    @Mock
    private lateinit var fakeFirestoreDataRepository: FakeFirestoreDataRepository
    @Mock
    private lateinit var userDataRepository: UserDataRepository
    @Mock
    private lateinit var authUI: AuthUI

    private val listOfProperty = listOf(Property("001", "025","House", 0,250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25"),
            Property("002", "025","House", 0,185000, 75, 3, 1, 2, "Little house", true, "2020-03-09T12:20:25"))

    private val listOfUser = listOf(User("566413", "Martin", "martin@orange.fr", "profile.martin/image.fr"), User("6534556", "John", "john@orange.fr", "profile.john/image.fr"))
    private val address1 = Address("1", 4, "allée des Bleuets", "71500", "Louhans", "France", "001")
    private val address2 = Address("2", 22, "rue des Lilas", "01350", "Culoz", "France", "002")


    private val liveData = MutableLiveData<List<Property>>()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testCoroutineDispatcher)
        liveData.postValue(listOfProperty)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testCoroutineDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getUserInformation() = runBlockingTest {
        val user = User("566413", "Martin", "martin@orange.fr", "profile.martin/image.fr")
        val mockUserDataRepository = mock<UserDataRepository> {
            onBlocking { getUserById("566413") } doReturn user
        }
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }

        val viewModel = MainViewModel(authUI, context, mockPropertyDataRepository, addressDataRepository, fakeFirestoreDataRepository, mockUserDataRepository)
        viewModel.getUserById("566413")
        val result = viewModel.userLiveData.getOrAwaitValue()
        assertEquals("martin@orange.fr", result?.email)
        assertEquals("profile.martin/image.fr", result?.photo)
    }

    @Test
    fun getAddressOfPropertyWithIdEqual001() {
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }
        val address = Address("1", 4, "allée des Bleuets", "71500", "Louhans", "France", "001")
        val mockAddressDataRepository = mock<AddressDataRepository> {
            onBlocking { getAddressOfOneProperty("001")} doReturn address
        }

        val viewModel = MainViewModel(authUI, context, mockPropertyDataRepository, mockAddressDataRepository, fakeFirestoreDataRepository, userDataRepository)
        viewModel.getAddressOfOneProperty("001")
        val result = viewModel.addressLiveData.getOrAwaitValue()
        assertEquals(4, result?.number)
    }

    @Test
    fun updateDatabaseWhenListFromRoomDatabaseIsEmpty() = runBlockingTest {
        val mockFirestoreDataRepository = mock<FirestoreDataRepository> {
            onBlocking { getAllPropertyFromFirestore() } doReturn listOfProperty
            onBlocking { getAllUsersFromFirestore() } doReturn listOfUser
            onBlocking { getAddressFromFirestore("001") } doReturn address1
            onBlocking { getAddressFromFirestore("002") } doReturn address2
        }
        val liveData = MutableLiveData<List<Property>>()
        liveData.postValue(null)
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }

        val viewModel = MainViewModel(authUI, context, mockPropertyDataRepository, addressDataRepository, mockFirestoreDataRepository, userDataRepository)
        viewModel.updateDatabase()

        val result = viewModel.getAllProperty().getOrAwaitValue()
        assertEquals(2, result.size)
    }

    @Test
    fun updateHeaderIfInternetIsNotAvailable() {
        val connManager = Mockito.mock(ConnectivityManager::class.java)
        val networkInfo = Mockito.mock(NetworkInfo::class.java)
        val packageManager = Mockito.mock(PackageManager::class.java)

        Mockito.`when`(context.packageManager).thenReturn(packageManager)
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connManager)
        Mockito.`when`(connManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(false)

        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }
        val user = User("566413", "Martin", "martin@orange.fr", "profile.martin/image.fr")
        val mockUserDataRepository = mock<UserDataRepository> {
            onBlocking { getUserById("566413") } doReturn user
        }
        val viewModel = MainViewModel(authUI, context, mockPropertyDataRepository, addressDataRepository, fakeFirestoreDataRepository, mockUserDataRepository)

        val userResulted = viewModel.updateHeader("Martin", "abc.fr", "martin2@orange.fr", "566413").getOrAwaitValue()

        assertFalse(networkInfo.isConnected)
        assertEquals("profile.martin/image.fr", userResulted.photo)
        assertEquals("Martin", userResulted.name)
        assertEquals("martin@orange.fr", userResulted.email)
    }

    @Test
    fun updateHeaderIfInternetIsAvailable() {
        val connManager = Mockito.mock(ConnectivityManager::class.java)
        val networkInfo = Mockito.mock(NetworkInfo::class.java)
        val packageManager = Mockito.mock(PackageManager::class.java)

        Mockito.`when`(context.packageManager).thenReturn(packageManager)
        Mockito.`when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connManager)
        Mockito.`when`(connManager.activeNetworkInfo).thenReturn(networkInfo)
        Mockito.`when`(networkInfo.isConnected).thenReturn(true)

        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }
        val user = User("566413", "Martin", "martin@orange.fr", "profile.martin/image.fr")
        val mockUserDataRepository = mock<UserDataRepository> {
            onBlocking { getUserById("566413") } doReturn user
        }
        val viewModel = MainViewModel(authUI, context, mockPropertyDataRepository, addressDataRepository, fakeFirestoreDataRepository, mockUserDataRepository)

        val userResulted = viewModel.updateHeader("Martin", "abc.fr", "martin2@orange.fr", "566413").getOrAwaitValue()

        assertTrue(networkInfo.isConnected)
        assertEquals("abc.fr", userResulted.photo)
        assertEquals("Martin", userResulted.name)
        assertEquals("martin2@orange.fr", userResulted.email)
    }
}