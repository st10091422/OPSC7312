package com.opsc.opsc7312poe.ui.observer

import android.util.Log
import androidx.lifecycle.Observer
import com.opsc.opsc7312poe.api.data.Transaction
import com.opsc.opsc7312poe.ui.adapter.TransactionAdapter

class TransactionObserver (
    private val adapter: TransactionAdapter,            // Adapter for displaying transactions in a RecyclerView
    //private val amount: TextView                         // TextView for displaying the total balance
) : Observer<List<Transaction>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel
    override fun onChanged(value: List<Transaction>) {
        // Update the data in the CategoryListAdapter
        adapter.updateTransactions(value)
        //updateBalance(value)
        // Update the categories in the associated ActivityFragment

        Log.d("Transaction", "transactions retrieved: ${value.size}\n $value")
    }
}
