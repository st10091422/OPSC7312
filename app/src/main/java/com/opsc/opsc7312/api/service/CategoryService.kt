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
    // This function sends a POST request to the "category/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Category object as the request body and returns a Call object containing the created Category.
    @POST("category")
    fun createCategory(@Body category: Category): Call<Category>

    // Updates an existing category identified by its ID.
    // This function sends a PUT request to the "category/{id}" endpoint.
    // It requires an authorization token in the header, the category ID in the path, and a Category object in the request body.
    // Returns a Call object containing the updated Category.
    @PUT("category/{id}")
    fun updateCategory(@Path("id") id: String, @Body category: Category): Call<Category>

    // Deletes a category identified by its ID.
    // This function sends a DELETE request to the "category/{id}" endpoint.
    // It requires an authorization token in the header and the category ID in the path.
    // Returns a Call object with no content on successful deletion.
    @DELETE("category/{id}")
    fun deleteCategory(@Path("id") id: String): Call<Void>

}