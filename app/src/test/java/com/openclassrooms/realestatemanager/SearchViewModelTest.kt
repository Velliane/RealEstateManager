package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.data.UserDataRepository
import com.openclassrooms.realestatemanager.search.SearchViewModel
import com.openclassrooms.realestatemanager.utils.getOrAwaitValue
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
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
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel

    @Mock
    private lateinit var context: Context

    @Mock
    private lateinit var userDataRepository: UserDataRepository

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setup() {
        viewModel = SearchViewModel(context, userDataRepository)
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testCoroutineDispatcher)
    }

    private val testCoroutineDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Test
    fun ifContainsTestTrue() {
        val contains = true
        assertEquals(" AND ", viewModel.ifContains(contains))
    }

    @Test
    fun inContainsTestFalse() {
        val contains = false
        assertEquals(" WHERE ", viewModel.ifContains(contains))
    }

    @Test
    fun contructQueryResearchTestWithAllTypesSelected() {
        // search all types, with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        val queryExpected = "SELECT * FROM Property " +
                "WHERE price >= '150000' AND price <= '350000' " +
                "AND rooms_nbr >= '1' AND rooms_nbr <= '8' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3' " +
                "AND agent LIKE 'Marine'"
        assertEquals(queryExpected, viewModel.constructQueryResearch("Marine", 150000, 350000, emptyList(), emptyList(), emptyList(), 1, 8, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithTypeHouseSelected() {
        // search type house, with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        // locate in Lyon or Paris
        val queryExpected = "SELECT * FROM Property " +
                "INNER JOIN Address ON Property.id_property = Address.idProperty " +
                "WHERE type LIKE 'House' " +
                "AND price >= '300000' AND price <= '400000' " +
                "AND rooms_nbr >= '3' AND rooms_nbr <= '4' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3' " +
                "AND UPPER(Address.street) LIKE '%LYON%' OR UPPER(Address.street) LIKE '%PARIS%' " +
                "OR UPPER(Address.city) LIKE '%LYON%' OR UPPER(Address.city) LIKE '%PARIS%' " +
                "OR UPPER(Address.country) LIKE '%LYON%' OR UPPER(Address.country) LIKE '%PARIS%'" +
                " AND agent LIKE 'Jules'"
        assertEquals(queryExpected, viewModel.constructQueryResearch("Jules", 300000, 400000, arrayListOf("House"), arrayListOf("Lyon", "Paris"), emptyList(), 3, 4, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithTypeHouseAndLoftSelected() {
        // search type house and loft, with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        val queryExpected = "SELECT * FROM Property WHERE type IN ('House','Loft') " +
                "AND price >= '300000' AND price <= '400000' " +
                "AND rooms_nbr >= '3' AND rooms_nbr <= '4' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3' " +
                "AND agent LIKE 'Tom'"
        assertEquals(queryExpected, viewModel.constructQueryResearch("Tom", 300000, 400000, arrayListOf("House", "Loft"), emptyList(), emptyList(), 3, 4, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithTypeHouseSelectedAndNearbies() {
        // search type house , with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        // nearby bus and restaurant
        val queryExpected = "SELECT * FROM Property WHERE type LIKE 'House' " +
                "AND price >= '300000' AND price <= '400000' " +
                "AND rooms_nbr >= '3' AND rooms_nbr <= '4' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3' " +
                "AND nearby IN ('BUS','RESTAURANT') " +
                "AND agent LIKE 'Jules'"
        assertEquals(queryExpected, viewModel.constructQueryResearch("Jules", 300000, 400000, arrayListOf("House"), emptyList(), arrayListOf("BUS", "RESTAURANT"), 3, 4, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithOneNearby() {
        // search type house , with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        // nearby bus and restaurant
        val queryExpected = "SELECT * FROM Property WHERE " +
                "price >= '300000' AND price <= '400000' " +
                "AND rooms_nbr >= '3' AND rooms_nbr <= '4' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3' " +
                "AND nearby LIKE 'UNIVERSITY' " +
                "AND agent LIKE 'Jules'"
        assertEquals(queryExpected, viewModel.constructQueryResearch("Jules", 300000, 400000, emptyList(), emptyList(), arrayListOf("UNIVERSITY"), 3, 4, 2, 3))
    }


    @Test
    fun getAgents() = runBlockingTest {
        val list = arrayListOf(User("355475fd", "Manon Dupuis", "mn.dp@orangr.fr", "mn38/img.fr"), User("35645454gt", "Tom Roche", "tr@free.fr", "66/img.fr"))
        val mockUserDataRepository = mock<UserDataRepository> {
            onBlocking { getAllUsers() } doReturn list
        }
        viewModel = SearchViewModel(context, mockUserDataRepository)
        viewModel.getAllUser()

        val listFound = viewModel.userListLiveData.getOrAwaitValue()
        assertTrue(listFound.isNotEmpty())
        assertEquals("Manon Dupuis", listFound[0].name)
    }
}