package com.opsc.opsc7312poe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.opsc.opsc7312poe.MainActivity
import com.opsc.opsc7312poe.R
import com.opsc.opsc7312poe.api.data.Transaction
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.api.local.db.databasehelper.CategoryDatabaseHelper
import com.opsc.opsc7312poe.api.local.db.databasehelper.TransactionDatabaseHelper
import com.opsc.opsc7312poe.api.viewmodel.TransactionViewModel
import com.opsc.opsc7312poe.databinding.FragmentHomeBinding
import com.opsc.opsc7312poe.ui.adapter.TransactionAdapter
import com.opsc.opsc7312poe.ui.observer.HomeObserver


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    // Adapter for displaying transactions in a RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    // ViewModel for managing authentication data
    private lateinit var transactionViewModel: TransactionViewModel

    private lateinit var localUser: LocalUser

    private lateinit var transactionDatabaseHelper: TransactionDatabaseHelper
    private lateinit var cateDatabaseHelper: CategoryDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        localUser = LocalUser.getInstance(requireContext())

        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        transactionAdapter = TransactionAdapter { transaction ->
            redirectToDetails(transaction) // Redirect to transaction details
        }

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        transactionDatabaseHelper = TransactionDatabaseHelper(requireContext())

        cateDatabaseHelper = CategoryDatabaseHelper(requireContext())


        binding.transactionRecycleView.layoutManager = LinearLayoutManager(requireContext()) // Use LinearLayout for layout
        binding.transactionRecycleView.setHasFixedSize(true) // Improve performance with fixed size
        binding.transactionRecycleView.adapter = transactionAdapter // Set the adapter to display transaction items


        val currentUser = localUser.getUser()

        // If the token is valid, observe the category data through the ViewModel.
        if (currentUser != null) {
            getTransactions(currentUser.id)

        } else {
            // Handle the scenario where the token is null (e.g., log an error or show a message).
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }

        return binding.root
    }

    private fun getTransactions(userId: String){

        try {
            val transactions = transactionDatabaseHelper.getAllTransactions(userId)

            for (transaction in transactions){
                transaction.category = cateDatabaseHelper.getCategory(transaction.categoryid)!!
            }

            binding.noOfTransactions.setText("${transactions.size} transactions")

            binding.totalTransactionAmount.setText("R${totalTransaction(transactions)}")

            transactionAdapter.updateTransactions(transactions)
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error inserting transaction", e)
        }
    }

    private fun totalTransaction(value: List<Transaction>): Double{
        var total = 0.0

        for (transaction in value){
            total += transaction.amount
        }

        return total
    }
    // Method to observe the ViewModel for transaction-related data and status updates
    private fun getAllTransaction(userId: String) {
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel

        // Show a progress dialog to indicate loading state

        // Observe the status of the transaction fetching operation
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            if (status) {
                // Update the progress dialog for successful registration

                // Dismiss the dialog after 2 seconds and redirect to the login screen
                Toast.makeText(requireContext(), "Transactions retrieved!!", Toast.LENGTH_SHORT).show() // Show logout message
            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT).show() // Show logout message

            }
        }

        // Observe any messages from the ViewModel
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->

            // Log the message for debugging purposes
            Log.d("Transactions message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect

                Toast.makeText(requireContext(), "Connection Timeout!", Toast.LENGTH_SHORT).show() // Show logout message
                transactionViewModel.getAllTransactions(userId)

            }
        }

        transactionViewModel.transactionList.observe(viewLifecycleOwner,
            HomeObserver(transactionAdapter, binding.noOfTransactions, binding.totalTransactionAmount)
        )

        // Initial call to fetch all transactions for the user
        transactionViewModel.getAllTransactions(userId)
    }

    // Function to handle navigation to the transaction details screen
    private fun redirectToDetails(transaction: Transaction) {
        // Create a new instance of UpdateTransactionFragment to display transaction details
        val transactionDetailsFragment = TransactionDetailsFragment()
        val bundle = Bundle()
        //bundle.putParcelable("transaction", transaction) // Pass the selected transaction as an argument

        bundle.putString("id", transaction.id)
        bundle.putString("title", transaction.title)
        bundle.putDouble("amount", transaction.amount)
        bundle.putLong("date", transaction.date)
        bundle.putString("categoryId", transaction.categoryid)
        bundle.putString("description", transaction.description)

        transactionDetailsFragment.arguments = bundle

        // Navigate to the UpdateTransactionFragment
        changeCurrentFragment(transactionDetailsFragment)
    }
    // Helper function to change the current fragment in the activity.
    private fun changeCurrentFragment(fragment: Fragment) {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/52318195/how-to-change-fragment-kotlin
        // Marcos Maliki
        // https://stackoverflow.com/users/8108169/marcos-maliki
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

}