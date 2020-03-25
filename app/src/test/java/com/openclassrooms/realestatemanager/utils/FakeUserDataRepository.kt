package com.openclassrooms.realestatemanager.utils

import com.openclassrooms.realestatemanager.login.User
import com.openclassrooms.realestatemanager.login.UserDao
import com.openclassrooms.realestatemanager.login.UserDataRepository

class FakeUserDataRepository(userDao: UserDao): UserDataRepository(userDao) {

    override suspend fun getUserById(userId: String): User {
        return User("566413", "Martin", "martin@orange.fr", "profile.martin/image.fr")
    }
}