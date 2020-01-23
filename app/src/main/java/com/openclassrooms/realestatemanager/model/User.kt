package com.openclassrooms.realestatemanager.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (

        @PrimaryKey
        @ColumnInfo(name = "userId")
        var userId: String,

        @ColumnInfo(name = "name")
        var name: String,
        @ColumnInfo(name = "email")
        var email: String,
        @ColumnInfo(name = "photo")
        var photo: String?
)