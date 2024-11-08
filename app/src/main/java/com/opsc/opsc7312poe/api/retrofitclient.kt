package com.opsc.opsc7312poe.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object retrofitclient {
    // This object was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    // Base URL of the API, fetched from the application constants for consistency.

    // Base URL for the API, sourced from application constants to ensure uniformity.
    private const val BASE_URL = "https://savr-p8zk.onrender.com/api/v1/"

    // Lazily initializes the Retrofit instance with specified configurations for API communication.
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // A generic function to create an instance of the specified service class.
    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }
}