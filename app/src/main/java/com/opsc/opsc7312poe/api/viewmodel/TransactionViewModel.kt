package com.opsc.opsc7312poe.api.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.opsc.opsc7312poe.api.data.Ids
import com.opsc.opsc7312poe.api.data.SyncData
import com.opsc.opsc7312poe.api.data.SyncIds
import com.opsc.opsc7312poe.api.data.Transaction
import com.opsc.opsc7312poe.api.retrofitclient
import com.opsc.opsc7312poe.api.service.TransactionService
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume

class TransactionViewModel:ViewModel() {
    // Instance of the API service used to communicate with the backend for transaction-related operations
    var api: TransactionService = retrofitclient.createService()

    // LiveData object used to monitor the status of transaction operations (true if successful, false otherwise)
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // LiveData object used to hold messages such as success or error messages from operations
    val message: MutableLiveData<String> = MutableLiveData()

    // LiveData object that holds a list of transactions retrieved from the server
    val transactionList: MutableLiveData<List<Transaction>> = MutableLiveData()

    // Function to retrieve all transactions for a user

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun getAllTransactions(id: String) {
        val call = api.getTransactions(id)  // API call to fetch the user's transactions



        // Execute the API call asynchronously
        call.enqueue(object : Callback<List<Transaction>> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                // Check if the response is successful (status code 200-299)
                if (response.isSuccessful) {
                    val transactions = response.body()
                    transactions?.let {
                        // Update the LiveData with the retrieved list of transactions
                        transactionList.postValue(it)
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Transactions retrieved")  // Set a success message
                    }
                } else {
                    // Handle the case when the request was not successful (error codes like 400 or 500)
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                    transactionList.postValue(listOf())  // Clear the list if the operation fails
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                }
            }

            // Callback when the API call fails (e.g., due to network issues)
            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                transactionList.postValue(listOf())  // Clear the transaction list on failure
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    // Function to create a new transaction for a user

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun createTransaction(transaction: Transaction) {
        // Make an API call to create a new transaction
        api.createTransaction(transaction).enqueue(object : Callback<Transaction> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    val createdTransaction = response.body()
                    createdTransaction?.let {
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Transaction created: $it")  // Set a success message
                        Log.d("MainActivity", "Transaction created: $it")
                    }
                } else {
                    // Handle unsuccessful responses
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Callback when the API call fails
            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    // Function to update an existing transaction by its ID

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun updateTransaction( id: String, transaction: Transaction) {
        // Make an API call to update an existing transaction
        api.updateTransaction(id, transaction).enqueue(object : Callback<Transaction> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    val updatedTransaction = response.body()
                    updatedTransaction?.let {
                        status.postValue(true)  // Mark the operation as successful
                        message.postValue("Transaction updated: $it")  // Set a success message
                        Log.d("MainActivity", "Transaction updated: $it")
                    }
                } else {
                    // Handle unsuccessful responses
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Callback when the API call fails
            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    // Function to delete an existing transaction by its ID

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun deleteTransaction(id: String) {
        // Make an API call to delete a transaction
        api.deleteTransaction(id).enqueue(object : Callback<Void> {
            // Callback when the response is received successfully
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Check if the response is successful
                if (response.isSuccessful) {
                    status.postValue(true)  // Mark the operation as successful
                    message.postValue("Transaction deleted successfully.")  // Set a success message
                    Log.d("MainActivity", "Transaction deleted successfully.")
                } else {
                    // Handle unsuccessful responses
                    status.postValue(false)  // Mark the operation as failed
                    message.postValue("Request failed with code: ${response.code()}")  // Set an error message
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Callback when the API call fails
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")  // Log the error message
                status.postValue(false)  // Mark the operation as failed
                message.postValue(t.message)  // Set the error message in LiveData
            }
        })
    }

    suspend fun syncTransactionsSuspend(transactionList: List<Transaction>): Pair<Boolean, List<Ids>?> {
        // Convert the list of transactions to JSON

        val requestBody = SyncData(transactions = transactionList)
        val call = api.syncTransactions(requestBody)  // Make the API call to fetch goals

        return suspendCancellableCoroutine { continuation ->
            call.enqueue(object : Callback<SyncIds> {
                override fun onResponse(call: Call<SyncIds>, response: Response<SyncIds>) {
                    if (response.isSuccessful) {
                        // Resume coroutine with success and the ID mappings
                        continuation.resume(Pair(true, response.body()?.ids))
                        message.postValue("Categories synced successfully.")
                    } else {
                        // Log and parse error, then resume with failure and null ID mappings
                        val errorMessage = if (response.errorBody() != null) {
                            try {
                                val errorResponse = Gson().fromJson(response.errorBody()?.string(), SyncIds::class.java)
                                "Error syncing goals: ${errorResponse.message}"
                            } catch (e: Exception) {
                                "Request failed with code: ${response.code()}, but failed to parse error response."
                            }
                        } else {
                            "Request failed with code: ${response.code()}, message: ${response.message()}"
                        }

                        Log.e("CategorySync", errorMessage)
                        message.postValue(errorMessage)
                        continuation.resume(Pair(false, null))
                    }
                }

                override fun onFailure(call: Call<SyncIds>, t: Throwable) {
                    // Log error and resume coroutine with failure
                    val errorMessage = "Sync failed: ${t.message ?: "Unknown error"}\nCall: $call"
                    Log.e("CategorySync", errorMessage)
                    message.postValue(t.message)
                    continuation.resume(Pair(false, null))
                }
            })

            // Cancel call if the coroutine is cancelled
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }


    // Sends a request to create a new category for the user.
    // Takes a user token for authentication and a Category object.
    // Updates the `status` and `message` based on the success of the request.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    suspend fun getRemoteTransactionsSuspend(userId: String): List<Transaction>? {
        val call = api.getAllTransactions(userId)  // Make the API call to fetch goals

        return suspendCancellableCoroutine { continuation ->
            // Log the request URL for debugging
            val url = call.request().url.toString()
            Log.d("MainActivity", "Request URL: $url")

            // Execute the API call asynchronously
            call.enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                    if (response.isSuccessful) {
                        // Resume coroutine with the response body
                        continuation.resume(response.body())
                        message.postValue("Categories retrieved")
                    } else {
                        // Parse and log error, then resume with null
                        val errorMessage = if (response.errorBody() != null) {
                            try {
                                val errorResponse = Gson().fromJson(response.errorBody()?.string(), SyncIds::class.java)
                                "Error syncing goals: ${errorResponse.message}"
                            } catch (e: Exception) {
                                "Request failed with code: ${response.code()}, but failed to parse error response."
                            }
                        } else {
                            "Request failed with code: ${response.code()}, message: ${response.message()}"
                        }

                        Log.e("GoalSync", errorMessage)
                        message.postValue(errorMessage)
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    // Log the error and resume coroutine with null
                    val errorMessage = "Sync failed: ${t.message ?: "Unknown error"}\nCall: $call"
                    Log.e("GoalSync", errorMessage)
                    message.postValue(t.message)
                    continuation.resume(null)
                }
            })

            // Cancel call if the coroutine is cancelled
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }
}