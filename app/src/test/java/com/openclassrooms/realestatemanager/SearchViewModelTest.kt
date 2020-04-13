package com.openclassrooms.realestatemanager

import android.content.Context
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.openclassrooms.realestatemanager.search.SearchViewModel
import com.openclassrooms.realestatemanager.search.TypeEnum
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(JUnit4::class)
class SearchViewModelTest {

    private lateinit var viewModel: SearchViewModel
    @Mock
    private lateinit var context: Context

    @Rule
    @JvmField
    val mockitoRule:  MockitoRule = MockitoJUnit.rule()

    @Before
    fun setup(){
        viewModel = SearchViewModel(context)
    }

    @Test
    fun ifContainsTestTrue(){
        val contains = true
        assertEquals(" AND", viewModel.ifContains(contains))
    }

    @Test
    fun inContainsTestFalse(){
        val contains = false
        assertEquals(" WHERE", viewModel.ifContains(contains))
    }

    @Test
    fun contructQueryResearchTestWithAllTypesSelected(){
        // search all types, with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        val queryExpected = "SELECT * FROM Property " +
                "WHERE price >= '150000' AND price <= '350000' " +
                "AND rooms_nbr >= '1' AND rooms_nbr <= '8' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3'"
        assertEquals(queryExpected, viewModel.constructQueryResearch(150000, 350000, emptyList(), emptyList(), emptyList(), 1, 8, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithTypeHouseSelected(){
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
                "OR UPPER(Address.country) LIKE '%LYON%' OR UPPER(Address.country) LIKE '%PARIS%' "
        assertEquals(queryExpected, viewModel.constructQueryResearch(300000, 400000, arrayListOf("House"), arrayListOf("Lyon", "Paris"), emptyList(), 3, 4, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithTypeHouseAndLoftSelected(){
        // search type house and loft, with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        val queryExpected = "SELECT * FROM Property WHERE type IN ('House','Loft') " +
                "AND price >= '300000' AND price <= '400000' " +
                "AND rooms_nbr >= '3' AND rooms_nbr <= '4' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3'"
        assertEquals(queryExpected, viewModel.constructQueryResearch(300000, 400000, arrayListOf("House", "Loft"), emptyList(), emptyList(), 3, 4, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithTypeHouseSelectedAndNearbies(){
        // search type house , with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        // nearby bus and restaurant
        val queryExpected = "SELECT * FROM Property WHERE type LIKE 'House' " +
                "AND price >= '300000' AND price <= '400000' " +
                "AND rooms_nbr >= '3' AND rooms_nbr <= '4' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3' " +
                "AND nearby IN ('BUS','RESTAURANT')"
        assertEquals(queryExpected, viewModel.constructQueryResearch(300000, 400000, arrayListOf("House"), emptyList(), arrayListOf("BUS", "RESTAURANT"), 3, 4, 2, 3))
    }

    @Test
    fun constructQueryResearchTestWithOneNearby(){
        // search type house , with price between 150 000 and 350 000,
        // room between 1 and 8
        // bedrooms between 2 and 3
        // nearby bus and restaurant
        val queryExpected = "SELECT * FROM Property WHERE " +
                "price >= '300000' AND price <= '400000' " +
                "AND rooms_nbr >= '3' AND rooms_nbr <= '4' " +
                "AND bed_nbr >= '2' AND bed_nbr <= '3' " +
                "AND nearby LIKE 'UNIVERSITY'"
        assertEquals(queryExpected, viewModel.constructQueryResearch(300000, 400000, emptyList(), emptyList(), arrayListOf("UNIVERSITY"), 3, 4, 2, 3))
    }

    @Test
    fun getTypesEnumListTest(){
        whenever(context.getString(TypeEnum.HOUSE.res)) doReturn "House"
        whenever(context.getString(TypeEnum.APARTMENT.res)) doReturn "Apartment"
        whenever(context.getString(TypeEnum.DUPLEX.res)) doReturn "Duplex"
        whenever(context.getString(TypeEnum.LOFT.res)) doReturn "Loft"
        whenever(context.getString(TypeEnum.VILLA.res)) doReturn "Villa"
        val list = arrayListOf(context.getString(TypeEnum.HOUSE.res), context.getString(TypeEnum.APARTMENT.res), context.getString(TypeEnum.DUPLEX.res), context.getString(TypeEnum.LOFT.res), context.getString(TypeEnum.VILLA.res))
        val listTest = viewModel.getTypesResList()
        assertEquals(list, listTest)
    }
}