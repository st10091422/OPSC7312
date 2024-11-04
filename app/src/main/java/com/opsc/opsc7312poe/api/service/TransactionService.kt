package com.opsc.opsc7312poe.api.service

import com.opsc.opsc7312poe.api.data.SyncData
import com.opsc.opsc7312poe.api.data.SyncIds
import com.opsc.opsc7312poe.api.data.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface TransactionService {
    @GET("transaction/{id}")
    fun getTransactions( @Path("id") userId: String): Call<List<Transaction>>

    // Creates a new transaction based on the provided transaction details.
    @POST("transaction")
    fun createTransaction(@Body transaction: Transaction): Call<Transaction>

    // Updates an existing transaction identified by its ID.
    @PUT("transaction/{id}")
    fun updateTransaction( @Path("id") id: String, @Body transaction: Transaction): Call<Transaction>

    // Deletes a transaction identified by its ID.
    @DELETE("transaction/{id}")
    fun deleteTransaction( @Path("id") id: String): Call<Void>

    @POST("transaction-all/sync")
    fun syncTransactions(@Body transactions: SyncData): Call<SyncIds>

    @POST("transaction-all/{id}")
    fun getAllTransactions( @Path("id") userId: String): Call<List<Transaction>>
}