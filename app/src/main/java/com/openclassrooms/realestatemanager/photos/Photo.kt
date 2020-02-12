package com.openclassrooms.realestatemanager.photos

import android.net.Uri

data class Photo(

        var uri: Uri? = null,
        var description: String? = null
)