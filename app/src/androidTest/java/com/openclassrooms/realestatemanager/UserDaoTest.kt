package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.runner.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.database.PropertyDatabase
import com.openclassrooms.realestatemanager.model.User
import junit.framework.Assert.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var propertyDatabase: PropertyDatabase
    private val USER_ID: String = "1"
    private val userTest = User(USER_ID, "Eric", "eric.martin@orange.fr", "http://5454544455")

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    @Throws(Exception::class)
    fun initDb(){
        propertyDatabase = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, PropertyDatabase::class.java)
                .allowMainThreadQueries().build()
    }

    @After
    @Throws(Exception::class)
    fun closeDb(){
        propertyDatabase.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertUser(){
        propertyDatabase.userDao().addUser(userTest)

        val user = LiveDataTestUtil.getValue(propertyDatabase.userDao().getUserById(USER_ID))

        assertTrue(user.name == userTest.name)
    }
}