package com.opsc.opsc7312.api.local

import android.content.Context
import android.content.SharedPreferences
import com.opsc.opsc7312.api.data.User

class LocalUser private constructor(context: Context) {
    // SharedPreferences instance to store user data
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Save user data to shared preferences
    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun saveUser(user: User, expiresIn: Long) {
        // Create an editor to modify shared preferences
        with(sharedPreferences.edit()) {
            // Store user ID, username, and email
            putString("userid", user.id)
            putString("username", user.username)
            putString("email", user.email)
            putLong("expires_in", expiresIn)

            // Apply changes asynchronously
            apply()
        }
    }

    // Retrieve user data from shared preferences
    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun getUser(): User? {
        // Get user details from shared preferences
        val userid = sharedPreferences.getString("userid", null)
        val username = sharedPreferences.getString("username", null)
        val email = sharedPreferences.getString("email", null)
        val expirationTime = sharedPreferences.getLong("expires_in", 0L)

        // Create a User object; if any value is missing or the token is expired, return null
        return if (userid != null && username != null && email != null && !isTokenExpired(expirationTime)) {
            User(id = userid, username = username, email = email)
        } else {
            null
        }
    }

    // Get token expiration time
    fun getTokenExpirationTime(): Long {
        return sharedPreferences.getLong("expires_in", 0L)
    }

    // Clear user data from shared preferences (e.g., on logout)
    // This method was adapted from stackoverflow
    // https://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
    // Harneet Kaur
    // https://stackoverflow.com/users/1444525/harneet-kaur
    // Ziem
    // https://stackoverflow.com/posts/11027631/revisions
    fun clearUser() {
        // Create an editor to modify shared preferences
        with(sharedPreferences.edit()) {
            // Remove all user data at once
            clear()

            // Apply changes asynchronously
            apply()
        }
    }

    // Check if the token is expired
    fun isTokenExpired(expirationTime: Long): Boolean {
        // Check if the current time has passed the expiration time (i.e., the token is expired)
        return System.currentTimeMillis() >= expirationTime
    }

    // Companion object for singleton instance management
    companion object {
        // This object was adapted from stackoverflow
        // https://stackoverflow.com/questions/40398072/singleton-with-parameter-in-kotlin
        // aminography
        // https://stackoverflow.com/users/1631967/aminography
        @Volatile
        private var INSTANCE: LocalUser? = null

        // Get the singleton instance of UserManager
        fun getInstance(context: Context): LocalUser {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalUser(context).also { INSTANCE = it }
            }
        }
    }
}
