package com.openclassrooms.realestatemanager

import android.content.Context
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property
import com.openclassrooms.realestatemanager.data.AddressDataRepository
import com.openclassrooms.realestatemanager.data.PropertyDataRepository
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDataRepository
import com.openclassrooms.realestatemanager.show.MainViewModel
import com.openclassrooms.realestatemanager.utils.FakeAddressDataRepository
import com.openclassrooms.realestatemanager.utils.FakeFirestoreDataRepository
import com.openclassrooms.realestatemanager.utils.FakePropertyDataRepository
import com.openclassrooms.realestatemanager.utils.FakeUserDataRepository
import junit.framework.Assert.assertEquals
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
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.LooperMode


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MainViewModelTest {

    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var fakePropertyDataRepository: FakePropertyDataRepository
    @Mock
    private lateinit var fakeAddressDataRepository: FakeAddressDataRepository
    @Mock
    private lateinit var fakeFirestoreDataRepository: FakeFirestoreDataRepository
    @Mock
    private lateinit var fakeUserDataRepository: FakeUserDataRepository

    private var listOfProperty = listOf(Property("001", "025","House", 250500, 125, 4, 2, 2, "Big house", true, "2020-03-12T12:20:25"),
            Property("002", "025","House", 185000, 75, 3, 1, 2, "Little house", true, "2020-03-09T12:20:25"))
    private val liveData = MutableLiveData<List<Property>>()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()
    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testCoroutineDispatcher)
        liveData.value = listOfProperty
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

        val viewModel = MainViewModel(context, mockPropertyDataRepository, fakeAddressDataRepository, fakeFirestoreDataRepository, mockUserDataRepository)
        viewModel.getUserById("566413")

        shadowOf(Looper.getMainLooper()).idle()
        assertEquals("martin@orange.fr", viewModel.userLiveData.value?.email)
        assertEquals("profile.martin/image.fr", viewModel.userLiveData.value?.photo)
    }

    @Test
    fun getAddressOfPropertyWithIdEqual001() {
        val mockPropertyDataRepository = mock<PropertyDataRepository> {
            onBlocking { getAllProperties() } doReturn liveData
        }
        val address = Address("1", 4, "allée des Bleuets", "71500", "Louhans", "France", "001")
        val mockAddressDataRepository = mock<AddressDataRepository> {
            onGeneric { getAddressOfOneProperty("001")} doReturn address
        }


        val viewModel = MainViewModel(context, mockPropertyDataRepository, mockAddressDataRepository, fakeFirestoreDataRepository, fakeUserDataRepository)
        viewModel.getAddressOfOneProperty("001")

        shadowOf(Looper.getMainLooper()).idle()
        assertEquals(4, viewModel.addressLiveData.value?.number)
    }

}