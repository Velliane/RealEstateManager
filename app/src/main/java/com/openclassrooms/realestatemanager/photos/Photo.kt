package com.openclassrooms.realestatemanager.photos

import android.net.Uri
import java.io.File

data class Photo(

        var uri: Uri? = null,
        var description: String? = null
)