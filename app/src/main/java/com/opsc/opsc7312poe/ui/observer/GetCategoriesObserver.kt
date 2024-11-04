package com.opsc.opsc7312poe.ui.observer

import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.Observer
import com.opsc.opsc7312poe.R
import com.opsc.opsc7312poe.api.data.Category
import com.opsc.opsc7312poe.ui.fragment.CreateTransactionFragment
import com.opsc.opsc7312poe.ui.fragment.TransactionDetailsFragment

class GetCategoriesObserver(
    private val fragment: CreateTransactionFragment?,
    private val detailsFragment: TransactionDetailsFragment?,
    private val spinner: Spinner,
    private val categoryId: String?
): Observer<List<Category>> {


    // Method called when the observed data changes
    override fun onChanged(value: List<Category>) {
        // initialize first element as the create button

        fragment?.updateCategories(value)
        detailsFragment?.updateCategories(value)
        // Create an ArrayAdapter with the populated data

        if(fragment != null){
            val customAdapter = ArrayAdapter(
                fragment.requireContext(),
                R.layout.custom_spinner_item,
                value.map { it.name } )// Use category names for display
            spinner.adapter = customAdapter
        }

        if(detailsFragment != null){
            val customAdapter = ArrayAdapter(
                detailsFragment.requireContext(),
                R.layout.custom_spinner_item,
                value.map { it.name } )// Use category names for display
            spinner.adapter = customAdapter
        }

        if(categoryId != null){
            // Set the spinner selection based on the categoryId
            categoryId.let {
                val position = value.indexOfFirst { category -> category.id == it }
                if (position >= 0) {
                    spinner.setSelection(position)
                }
            }
        }


        Log.d("Category", "Category retrieved: ${value.size}\n $value")
    }
}