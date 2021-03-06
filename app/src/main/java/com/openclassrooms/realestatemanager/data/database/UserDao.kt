package com.openclassrooms.realestatemanager.data.database

import androidx.room.*
import com.openclassrooms.realestatemanager.login.User


/**
 * DAO Interface to group all CRUD requests for the table User of the PropertyDatabase
 */

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    suspend fun getAllUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: User): Long

    @Update
    fun updateUser(user: User): Int

    @Query("SELECT * FROM User WHERE userId = :userId")
    suspend fun getUserById(userId: String): User
}