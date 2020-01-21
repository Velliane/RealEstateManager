package com.openclassrooms.realestatemanager

import android.content.res.Configuration
import com.openclassrooms.realestatemanager.utils.getScreenOrientation
import junit.framework.Assert.assertEquals
import org.junit.Test

class ScreenOrientationTest {

    @Test
    fun getScreenOrientation_Portrait() {
        val orientation: Int = Configuration.ORIENTATION_PORTRAIT
        assertEquals(false, getScreenOrientation(orientation))
    }

    @Test
    fun getScreenOrientation_Landscape() {
        val orientation: Int = Configuration.ORIENTATION_LANDSCAPE
        assertEquals(true, getScreenOrientation(orientation))
    }
}