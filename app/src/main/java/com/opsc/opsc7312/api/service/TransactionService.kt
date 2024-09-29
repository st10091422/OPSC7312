package com.opsc.opsc7312.api.service

import com.opsc.opsc7312.api.data.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TransactionService {
    @GET("transaction/{id}")
    fun getTransactions(@Header("Authorization") token: String, @Path("id") userId: String): Call<List<Transaction>>

    // Creates a new transaction based on the provided transaction details.
    // This method sends a POST request to the "transaction/create" endpoint.
    // An authorization token is required in the header.
    // It takes a Transaction object as the request body and returns a Call object containing the created Transaction.
    @POST("transaction")
    fun createTransaction(@Body transaction: Transaction): Call<Transaction>

    // Updates an existing transaction identified by its ID.
    // This method sends a PUT request to the "transaction/{id}" endpoint.
    // It requires an authorization token in the header, the transaction ID in the path, and a Transaction object in the request body.
    // Returns a Call object containing the updated Transaction.
    @PUT("transaction/{id}")
    fun updateTransaction( @Path("id") id: String, @Body transaction: Transaction): Call<Transaction>

    // Deletes a transaction identified by its ID.
    // This method sends a DELETE request to the "transaction/{id}" endpoint.
    // It requires an authorization token in the header and the transaction ID in the path.
    // Returns a Call object with no content on successful deletion.
    @DELETE("transaction/{id}")
    fun deleteTransaction( @Path("id") id: String): Call<Void>

}