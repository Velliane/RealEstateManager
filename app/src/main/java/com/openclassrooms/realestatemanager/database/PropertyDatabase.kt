package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.database.dao.UserDao
import com.openclassrooms.realestatemanager.model.Property
import com.openclassrooms.realestatemanager.model.User

@Database(
        entities = [Property::class, User::class],
        version = 1,
        exportSchema = false
)

abstract class PropertyDatabase : RoomDatabase() {

    abstract fun propertyDao(): PropertyDao
    abstract fun userDao(): UserDao

    companion object {
        private var INSTANCE: PropertyDatabase? = null

        fun getInstance(context: Context): PropertyDatabase {
            if (INSTANCE == null) {
                synchronized(PropertyDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                                context.applicationContext,
                                PropertyDatabase::class.java,
                                "Books.db"
                        )
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