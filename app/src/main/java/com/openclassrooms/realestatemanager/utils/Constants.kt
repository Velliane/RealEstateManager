package com.openclassrooms.realestatemanager.utils

class Constants {

    companion object {

        // BUNDLE
        const val PROPERTY_ID = "PROPERTY_ID"
        const val SEARCH_QUERY = "SEARCH_QUERY"

        // INTENT
        const val RESULT = "RESULT"

        // REQUEST CODE
        const val RC_SIGN_IN = 1
        const val RC_PERMISSION_LOCATION = 2
        const val RC_PERMISSION_FILES_STORAGE = 3
        const val RC_CHOOSE_IMAGE = 4
        const val RC_SEARCH = 5
        const val RC_CAMERA = 6

        // PERMISSIONS
        const val PERMS = android.Manifest.permission.READ_EXTERNAL_STORAGE

        // KEY FOR SAVE INSTANCE
        const val KEY_TEXT_PRICE = "KEY_TEXT_PRICE"
        const val KEY_TEXT_SURFACE = "KEY_TEXT_SURFACE"
        const val KEY_TEXT_DESCRIPTION = "KEY_TEXT_DESCRIPTION"
        const val KEY_TEXT_ROOMS = "KEY_TEXT_ROOMS"
        const val KEY_TEXT_BEDROOMS = "KEY_TEXT_BEDROOMS"
        const val KEY_TEXT_BATHROOMS = "KEY_TEXT_BATHROOMS"
        const val KEY_TEXT_TYPE = "KEY_TEXT_TYPE"
        const val KEY_TEXT_AGENT = "KEY_TEXT_AGENT"

        // FIRESTORE COLLECTIONS
        const val COLLECTION_USERS = "users"
        const val COLLECTION_PROPERTIES = "properties"
        const val COLLECTION_ADDRESS = "addresses"

        // SHARED PREFERENCES
        const val SHARED_PREFERENCES = "SHARED_PREFERENCES"
        const val PREF_ID_USER = "PREF_ID_USER"
        const val PREF_ID_PROPERTY = "PREF_ID_PROPERTY"
        const val PREF_CURRENCY = "PREF_CURRENCY"

        // NOTIFICATION DATA
        const val DATA_USER_NAME = "DATA_USER_NAME"


    }
}

