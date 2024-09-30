package com.opsc.opsc7312.api.data

data class Transaction(
    var id: String = "",                // Unique ID for the transaction
    var title: String = "",              // Name or description of the transaction
    var amount: Double = 0.00,          // Amount of money for the transaction
    var date: Long = 0L,                // Transaction date in milliseconds (Epoch time)
    var userid: String = "",            // User ID who made the transaction
    var categoryid: String = "",        // ID of the category associated with the transaction
    var description: String = "",
    val category: Category = Category() // Category object representing the category details of the transaction
)
