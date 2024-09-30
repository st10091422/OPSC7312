package com.opsc.opsc7312.ui.fragment

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
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.api.data.Transaction
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.api.viewmodel.TransactionViewModel
import com.opsc.opsc7312.databinding.FragmentCreateTransactionBinding
import com.opsc.opsc7312.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312.ui.adapter.TransactionAdapter
import com.opsc.opsc7312.ui.observer.TransactionObserver


class TransactionsFragment : Fragment() {

    private lateinit var binding: FragmentTransactionsBinding

    // Adapter for displaying transactions in a RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    // ViewModel for managing authentication data
    private lateinit var transactionViewModel: TransactionViewModel

    private lateinit var localUser: LocalUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTransactionsBinding.inflate(layoutInflater)

        localUser = LocalUser.getInstance(requireContext())

        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        transactionAdapter = TransactionAdapter { transaction ->
            redirectToDetails(transaction) // Redirect to transaction details
        }

        binding.backButton.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        binding.add.setOnClickListener {
            changeCurrentFragment(CreateTransactionFragment())
        }

        binding.transactionRecycleView.layoutManager = LinearLayoutManager(requireContext()) // Use LinearLayout for layout
        binding.transactionRecycleView.setHasFixedSize(true) // Improve performance with fixed size
        binding.transactionRecycleView.adapter = transactionAdapter // Set the adapter to display transaction items


        val currentUser = localUser.getUser()

        // If the token is valid, observe the category data through the ViewModel.
        if (currentUser != null) {
            getAllTransaction(currentUser.id)

        } else {
            // Handle the scenario where the token is null (e.g., log an error or show a message).
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }

        return binding.root
    }

    // Method to observe the ViewModel for transaction-related data and status updates
    private fun getAllTransaction(userId: String) {
        // Show a progress dialog to indicate loading state

        // Observe the status of the transaction fetching operation
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // Handle changes in the status (indicates success or failure)

            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel
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
            // Check for timeout or inability to resolve host
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Log the message for debugging purposes
            Log.d("Transactions message", message)

            // Check for specific messages that indicate a timeout or network issue
            if (message == "timeout" || message.contains("Unable to resolve host")) {
                // Show a timeout dialog and attempt to reconnect

                Toast.makeText(requireContext(), "Connection Timeout!", Toast.LENGTH_SHORT).show() // Show logout message
                transactionViewModel.getAllTransactions(userId)

            }
        }

        // Observe the transaction list and set up a custom observer to handle changes
        // Check for timeout or inability to resolve host
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        transactionViewModel.transactionList.observe(viewLifecycleOwner,
            TransactionObserver(transactionAdapter)
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