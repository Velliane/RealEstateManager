package com.openclassrooms.realestatemanager.login

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.login.User


/**
 * DAO Interface to group all CRUD requests for the table Agent of the PropertyDatabase
 */

@Dao
interface UserDao {

    @Query("SELECT * FROM User")
    fun getAllUsers(): LiveData<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addUser(user: User): Long

    @Update
    fun updateUser(user: User): Int

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUserById(userId: String): LiveData<User>
}