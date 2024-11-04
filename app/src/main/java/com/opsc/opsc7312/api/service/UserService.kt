package com.opsc.opsc7312.api.service

import com.opsc.opsc7312.api.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @POST("user/register")
    fun register(@Body user: User): Call<User>

    // Authenticates a user using their login credentials.
    @POST("user/login")
    fun login(@Body user: User): Call<User>

    // Registers a new user via Single Sign-On (SSO) with the provided user details.
    @POST("user/sso-register")
    fun registerWithSSO(@Body user: User): Call<User>

    // Authenticates a user via Single Sign-On (SSO) using their credentials.
    @POST("user/sso-login")
    fun loginWithSSO(@Body user: User): Call<User>

    // Updates the email and username of a specific user identified by their user ID.
    @PUT("user/{id}")
    fun updateUser(@Path("id") uid: String, @Body user: User): Call<User>


}