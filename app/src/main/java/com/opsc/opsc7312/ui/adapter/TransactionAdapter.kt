package com.opsc.opsc7312.ui.adapter

import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.R
import com.opsc.opsc7312.api.data.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// This class adapted from geeksforgeeks
// https://www.geeksforgeeks.org/android-recyclerview/
// BaibhavOjha
// https://auth.geeksforgeeks.org/user/BaibhavOjha/articles?utm_source=geeksforgeeks&utm_medium=article_author&utm_campaign=auth_user

class TransactionAdapter(private val onItemClick: (Transaction) -> Unit) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    // Mutable list to hold the transactions
    private val transactions = mutableListOf<Transaction>()

    // Function to update the adapter's data with a new list of transactions
    fun updateTransactions(data: List<Transaction>) {
        transactions.clear() // Clear the existing transactions
        transactions.addAll(data) // Add new transactions to the list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    val CategoryIcons = mapOf(
        "groceries" to R.drawable.baseline_shopping_cart_24,
        "education" to R.drawable.baseline_school_24,
        "travel" to R.drawable.baseline_airplanemode_active_24,
        "transportation" to R.drawable.baseline_directions_bus_24,
        "healthcare" to R.drawable.baseline_medical_services_24,
        "gift" to R.drawable.baseline_card_giftcard_24
    )

    // Create a new ViewHolder for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the layout for each transaction item and create a ViewHolder
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item_layout, parent, false)
        return ViewHolder(itemView) // Return the newly created ViewHolder
    }

    // Get the total number of transactions
    override fun getItemCount(): Int {
        return transactions.size // Return the size of the transactions list
    }

    // Bind the transaction data to the ViewHolder at the specified position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.itemView.setOnClickListener {
            onItemClick(transaction)
        }


        // If categorized, use the category's color
        val image = CategoryIcons[transaction.category.icon]!!
        // Set the icon without tinting
        holder.iconImage.setImageResource(image)

        // Set transaction details
        holder.transactionName.text = transaction.title
        holder.date.text = longToDate(transaction.date)
        holder.amount.text = "R${String.format(Locale.US, "%.2f", transaction.amount)}"
    }

    fun longToDate(timestamp: Long): String {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = sdf.format(date)
        return formattedDate
    }




    // ViewHolder class to hold the views for each transaction item
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ImageView for displaying the transaction icon
        val iconImage: ImageView = itemView.findViewById(R.id.card_img)
        // TextView for displaying the transaction name
        val transactionName: TextView = itemView.findViewById(R.id.name)
        // TextView for indicating if the transaction is recurring
        val date: TextView = itemView.findViewById(R.id.date)
        // TextView for displaying the transaction amount
        val amount: TextView = itemView.findViewById(R.id.amount)
    }
}