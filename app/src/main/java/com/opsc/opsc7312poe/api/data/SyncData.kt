package com.opsc.opsc7312poe.api.data

data class SyncData(
    var categories: List<Category> = listOf(),
    var transactions: List<Transaction> = listOf()

)
