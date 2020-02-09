package com.openclassrooms.realestatemanager.property

import android.net.Uri

data class Photo(

        var uri: Uri? = null,
        var description: String? = null
)