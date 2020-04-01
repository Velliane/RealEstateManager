package com.openclassrooms.realestatemanager

import android.content.Context
import com.nhaarman.mockitokotlin2.given
import com.openclassrooms.realestatemanager.settings.SettingsViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotSame
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


@RunWith(JUnit4::class)
class SettingsViewModelTest {


    private lateinit var settingsViewModelTest: SettingsViewModel

    @Mock
    private lateinit var context: Context

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        settingsViewModelTest = SettingsViewModel(context)
    }

    @Test
    fun getListOfCurrencyAvailable() {
        given(context.getString(R.string.settings_euro)).willReturn("Euro")
        given(context.getString(R.string.settings_dollar)).willReturn("Dollar")
        val list = arrayListOf("Euro", "Dollar")
        assertEquals(list, settingsViewModelTest.getCurrencyList())
        assertNotSame("Euro", settingsViewModelTest.getCurrencyList())
    }
}