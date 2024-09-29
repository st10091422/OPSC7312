package com.opsc.opsc7312.api.data

data class User(
    var id: String = "",               // Unique ID for the user
    var username: String = "",          // The username for the user's account
    var email: String = "",             // The user's email address
    var password: String = "",
    var message: String = ""
)
