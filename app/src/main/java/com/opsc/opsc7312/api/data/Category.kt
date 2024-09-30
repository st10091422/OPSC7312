package com.opsc.opsc7312.api.data

data class Category(
    var id: String = "",
    var name: String = "",
    var icon: String = "",
    var userid: String = "",
    val isCreateButton: Boolean = false
)