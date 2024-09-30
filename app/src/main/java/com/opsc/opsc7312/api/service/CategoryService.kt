package com.opsc.opsc7312.api.service

import com.opsc.opsc7312.api.data.Category
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryService {
    @GET("category/{id}")
    fun getCategories(@Path("id") userId: String): Call<List<Category>>

    // Creates a new category based on the provided category details.
    @POST("category")
    fun createCategory(@Body category: Category): Call<Category>

    // Updates an existing category identified by its ID.
    @PUT("category/{id}")
    fun updateCategory(@Path("id") id: String, @Body category: Category): Call<Category>

    // Deletes a category identified by its ID.
    @DELETE("category/{id}")
    fun deleteCategory(@Path("id") id: String): Call<Void>

}