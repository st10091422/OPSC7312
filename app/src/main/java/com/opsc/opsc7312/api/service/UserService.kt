package com.opsc.opsc7312.api.service

import com.opsc.opsc7312.api.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("user/register")
    fun register(@Body user: User): Call<User>

    // Authenticates a user using their login credentials.
    // This function sends a POST request to the "auth/signin" endpoint.
    // Upon success, it returns a Call object containing the User data for the logged-in user.
    @POST("user/login")
    fun login(@Body user: User): Call<User>

    // Registers a new user via Single Sign-On (SSO) with the provided user details.
    // It sends a POST request to the "auth/signup-sso" endpoint.
    // Returns a Call object containing the User data of the newly registered user.
    @POST("user/sso-register")
    fun registerWithSSO(@Body user: User): Call<User>

    // Authenticates a user via Single Sign-On (SSO) using their credentials.
    // This function sends a POST request to the "auth/signin-sso" endpoint.
    // Returns a Call object containing the User data for the logged-in user.
    @POST("user/sso-login")
    fun loginWithSSO(@Body user: User): Call<User>

}