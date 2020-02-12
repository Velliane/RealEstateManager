package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.parseLocalDateTimeToString
import com.openclassrooms.realestatemanager.utils.parseStringDateToLocalDateTime
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month

class DateUtilsTest {

    private val date1 = LocalDateTime.of(2020, Month.JANUARY, 12, 12, 20 ,25)
    private val date2 = LocalDateTime.of(2020, Month.JANUARY, 12, 12, 35 ,25)

    @Test
    fun parseLocalDateToString(){
        assertEquals("2020-01-12T12:20:25", parseLocalDateTimeToString(date1))
        assertEquals("2020-01-12T12:35:25", parseLocalDateTimeToString(date2))
    }

    @Test
    fun parseStingDateToLocalDateTime(){
        assertEquals(date1, parseStringDateToLocalDateTime("2020-01-12T12:20:25"))
        assertEquals(date2, parseStringDateToLocalDateTime("2020-01-12T12:35:25"))
    }

}