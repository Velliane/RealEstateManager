package com.openclassrooms.realestatemanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Agent (

        @PrimaryKey(autoGenerate = true)
        var id_agent: Int,

        var name: String
)