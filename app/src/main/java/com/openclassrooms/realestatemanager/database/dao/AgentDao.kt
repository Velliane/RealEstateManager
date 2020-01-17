package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.model.Agent


/**
 * DAO Interface to group all CRUD requests for the table Agent of the PropertyDatabase
 */

@Dao
interface AgentDao {

    @Query("SELECT * FROM Agent")
    fun getAllAgents(): LiveData<List<Agent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addAgent(agent: Agent): Long

    @Update
    fun updateAgent(agent: Agent): Int

    @Query("SELECT * FROM Agent WHERE id_agent = :id_agent")
    fun getAgentById(id_agent: Int): Int
}