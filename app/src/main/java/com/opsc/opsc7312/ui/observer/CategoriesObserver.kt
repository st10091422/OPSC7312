package com.opsc.opsc7312.ui.observer

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312.api.data.Category
import com.opsc.opsc7312.ui.adapter.CategoryAdapter

class CategoriesObserver (private val adapter: CategoryAdapter
): Observer<List<Category>> {


    // Method called when the observed data changes
    override fun onChanged(value: List<Category>) {
        // initialize first element as the create button
        val cat0 = Category(isCreateButton = true)

        // Create a new list with cat0 as the first element
        val updatedCategories = listOf(cat0) + value

        // Update the data in the CategoryAdapter
        adapter.updateData(updatedCategories)

        Log.d("Category", "Category retrieved: ${value.size}\n $value")
    }
}