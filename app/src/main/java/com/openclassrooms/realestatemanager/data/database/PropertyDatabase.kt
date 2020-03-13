package com.openclassrooms.realestatemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.login.UserDao
import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.add_edit.Address
import com.openclassrooms.realestatemanager.add_edit.Property

@Database(
        entities = [Property::class, User::class, Address::class],
        version = 1,
        exportSchema = false
)

abstract class PropertyDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun userDao(): UserDao
    abstract fun addressDao(): AddressDao

    companion object {
        private var INSTANCE: PropertyDatabase? = null

        fun getInstance(context: Context): PropertyDatabase {
            if (INSTANCE == null) {
                synchronized(PropertyDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.applicationContext,
                                PropertyDatabase::class.java,
                                "Property.db"
                        ).setJournalMode(JournalMode.TRUNCATE)
                                .build()
                    }
                }
            }
            return INSTANCE as PropertyDatabase
        }

        fun destroyInstance(){
            INSTANCE = null
        }

    }

}