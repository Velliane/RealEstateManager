package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.property.Property
import com.openclassrooms.realestatemanager.utils.compareByDate
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateDatabaseTest {

    private val property1 = Property("1", "House", 250000, 85, 3, 1, 2, "Little house", "5 rue des acacias", true, "2020-01-12T12:20:25")
    private val property2 = Property("5", "House", 360000, 105, 4, 1, 3, "Big house", "25 rue des acacias", true, "2020-01-12T12:35:25")


    @Test
    fun compareByDate_ReturnMoreRecent(){
        assertEquals(property2, compareByDate(property1, property2))
    }

}