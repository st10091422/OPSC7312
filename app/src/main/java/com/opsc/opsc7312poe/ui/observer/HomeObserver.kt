package com.opsc.opsc7312poe.ui.observer

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import com.opsc.opsc7312poe.api.data.Transaction
import com.opsc.opsc7312poe.ui.adapter.TransactionAdapter

class HomeObserver(
    private val adapter: TransactionAdapter, // Adapter for displaying transactions in a RecyclerView
    private val noOfTransactions: TextView,
    private val totalTransactionAmount: TextView,
    //private val amount: TextView                         // TextView for displaying the total balance
) : Observer<List<Transaction>> {
    // This class was adapted from stackoverflow
    // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
    // Kevin Robatel
    // https://stackoverflow.com/users/244702/kevin-robatel
    override fun onChanged(value: List<Transaction>) {
        // Update the data in the CategoryListAdapter
        adapter.updateTransactions(value)

        noOfTransactions.setText("${value.size} transactiobs")

        totalTransactionAmount.setText("R${totalTransaction(value)}")

        Log.d("Transaction", "transactions retrieved: ${value.size}\n $value")
    }

    private fun totalTransaction(value: List<Transaction>): Double{
        var total = 0.0

        for (transaction in value){
            total += transaction.amount
        }

        return total
    }

}