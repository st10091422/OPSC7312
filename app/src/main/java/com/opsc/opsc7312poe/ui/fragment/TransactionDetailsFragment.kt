package com.opsc.opsc7312.ui.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.api.data.Category
import com.opsc.opsc7312.api.data.Transaction
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.api.viewmodel.CategoryViewModel
import com.opsc.opsc7312.api.viewmodel.TransactionViewModel
import com.opsc.opsc7312.databinding.FragmentTransactionDetailsBinding
import com.opsc.opsc7312.ui.observer.GetCategoriesObserver
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class TransactionDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTransactionDetailsBinding

    // ViewModel responsible for managing and processing the category data.
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var transactionViewModel: TransactionViewModel

    private lateinit var localUser: LocalUser
    private var selectedCategoryId: String? = null

    private lateinit var categories: MutableList<Category>

    private var categoryId: String = ""
    private var transId = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionDetailsBinding.inflate(layoutInflater)

        // Get the CategoryController ViewModel for interacting with category data.
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        categories = mutableListOf()
        localUser = LocalUser.getInstance(requireContext())

        binding.backButton.setOnClickListener {
            findNavController().navigateUp() // Navigate back using Navigation Component
        }

        // Set up the spinner's item selection listener
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                // Retrieve the selected category using the position index
                selectedCategoryId = categories[position].id // Get the ID directly from the list
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCategoryId = null // Reset if nothing is selected
            }
        }

        binding.save.setOnClickListener {
            addData()
        }

        binding.datePicker.setOnClickListener {
            showDatePickerDialog()
        }

        val currentUser = localUser.getUser()

        // If the token is valid, observe the category data through the ViewModel.
        if (currentUser != null) {
            getAllCategories(currentUser.id)
        } else {
            // Handle the scenario where the token is null (e.g., log an error or show a message).
            startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                )
            ) // Restart the MainActivity
        }

        setTransactionData()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setTransactionData() {
        val title = arguments?.getString("title")
        val amount = arguments?.getDouble("amount")
        val date = arguments?.getLong("date")
        categoryId = arguments?.getString("categoryId")!!
        val description = arguments?.getString("description")
        transId = arguments?.getString("id")!!

        binding.title.setText(title)
        binding.amount.setText(amount.toString())
        binding.txtDate.text = date?.let { longToDate(it) }
        binding.description.setText(description)

        // Set the spinner selection based on the categoryId
        categoryId.let {
            val position = categories.indexOfFirst { category -> category.id == it }
            if (position >= 0) {
                binding.spinner.setSelection(position)
            }
        }
    }

    fun updateCategories(data: List<Category>) {
        categories.clear()
        categories.addAll(data)
    }

    // Displays a date picker dialog to select a deadline
    private fun showDatePickerDialog() {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/45842167/how-to-use-datepickerdialog-in-kotlin
        // Derek
        // https://stackoverflow.com/users/8195525/derek
        try {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create and show the date picker dialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
                    // Update the TextView with the selected date
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    binding.txtDate.text = selectedDate
                },
                year,
                month,
                day
            )

            datePickerDialog.show()
        } catch (e: Exception) {
            // Print the stack trace if an exception occurs
            e.printStackTrace()
        }
    }

    private fun getAllCategories(id: String) {

        // Observe the status of the category data API call (success or failure).
        categoryViewModel.status.observe(viewLifecycleOwner) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle the result of the API call and dismiss the progress dialog accordingly.
            if (status) {
                // Update the progress dialog for successful registration

                // Dismiss the dialog after 2 seconds and redirect to the login screen
                Toast.makeText(requireContext(), "Categories retrieved!", Toast.LENGTH_SHORT)
                    .show() // Show logout message


            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT)
                    .show() // Show logout message

            }
        }

        // Observe the message LiveData to display any important information to the user.
        categoryViewModel.message.observe(viewLifecycleOwner) { message ->
            // Handle timeout errors or other issues related to network connectivity.
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(requireContext(), "Connection Timeout!", Toast.LENGTH_SHORT)
                    .show() // Show logout message
                categoryViewModel.getAllCategories(id)
            }
        }

        // Observe the category list and update the UI accordingly.
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        categoryViewModel.categoryList.observe(
            viewLifecycleOwner,
            GetCategoriesObserver(null, this, binding.spinner, categoryId)
        )

        // Initial API call to fetch all categories from the server.
        categoryViewModel.getAllCategories(id)
    }

    private fun addTransaction(id: String, transaction: Transaction) {

        // Observe the status of the category data API call (success or failure).
        transactionViewModel.status.observe(viewLifecycleOwner) { status ->
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            // Handle the result of the API call and dismiss the progress dialog accordingly.
            if (status) {
                // Update the progress dialog for successful registration

                // Dismiss the dialog after 2 seconds and redirect to the login screen
                Toast.makeText(requireContext(), "Transaction created!", Toast.LENGTH_SHORT)
                    .show() // Show logout message

            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT)
                    .show() // Show logout message

            }
        }

        // Observe the message LiveData to display any important information to the user.
        transactionViewModel.message.observe(viewLifecycleOwner) { message ->
            // Handle timeout errors or other issues related to network connectivity.
            // This observer implementation was adapted from stackoverflow
            // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
            // Kevin Robatel
            // https://stackoverflow.com/users/244702/kevin-robatel

            if (message == "timeout" || message.contains("Unable to resolve host")) {
                Toast.makeText(requireContext(), "Connection Timeout!", Toast.LENGTH_SHORT)
                    .show() // Show logout message
                transactionViewModel.updateTransaction(id, transaction)
            }
        }

        // Observe the category list and update the UI accordingly.
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel

        // Initial API call to fetch all categories from the server.
        transactionViewModel.updateTransaction(id, transaction)
    }

    private fun addData() {
        val currentUser = localUser.getUser()

        // If the token is valid, observe the category data through the ViewModel.
        if (currentUser != null) {
            val title = binding.title.text.toString()
            val amountString = binding.amount.text.toString()
            val date = binding.txtDate.text.toString()
            val description = binding.description.text.toString()

            // Sanitize the amount
            val sanitizedAmountString = sanitizeAmount(amountString)

            // Validate input data
            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Title is required!", Toast.LENGTH_SHORT).show()
                return
            }

            if (sanitizedAmountString.isEmpty()) {
                Toast.makeText(requireContext(), "Amount is required!", Toast.LENGTH_SHORT).show()
                return
            }

            // Check if the sanitized amount can be parsed to a double
            val amount = sanitizedAmountString.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(
                    requireContext(),
                    "Amount must be a positive number!",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (date.isEmpty()) {
                Toast.makeText(requireContext(), "Date is required!", Toast.LENGTH_SHORT).show()
                return
            }

            // Validate date format
            if (!isValidDate(date)) {
                Toast.makeText(requireContext(), "Invalid date format!", Toast.LENGTH_SHORT).show()
                return
            }

            // Check if the date is valid
            try {
                StringToLong(date) // This will throw if the date is invalid
            } catch (e: IllegalArgumentException) {
                Toast.makeText(
                    requireContext(),
                    "Invalid date format! Please use dd/MM/yyyy",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (selectedCategoryId == null) {
                Toast.makeText(requireContext(), "Please select a category!", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            // Assuming the selected category is an instance of Category, retrieve its ID
            val newTransaction = Transaction(
                title = title,
                amount = amount,
                userid = currentUser.id,
                description = description,
                date = StringToLong(date),
                categoryid = selectedCategoryId!! // Include the category ID in the transaction
            )

            // Proceed to add the transaction after validation
            addTransaction(transId, newTransaction)
        } else {
            // Handle the scenario where the token is null (e.g., log an error or show a message).
            startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                )
            ) // Restart the MainActivity
        }
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

    private fun StringToLong(dateString: String): Long {
        // This method was adapted from stackoverflow
        // https://stackoverflow.com/questions/57402045/how-to-format-in-kotlin-date-in-string-or-timestamp-to-my-preferred-format
        // https://stackoverflow.com/users/11555903/ben-shmuel
        // Ben Shmuel

        // Create a date format object for parsing the date string
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Parse the date string into a Date object
        val date: Date =
            dateFormat.parse(dateString) ?: throw IllegalArgumentException("Invalid date format")

        // Return the timestamp of the date
        return date.time
    }

    // Helper function to validate date format
    private fun isValidDate(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString)
            true // Valid date
        } catch (e: Exception) {
            false // Invalid date
        }
    }

    // Sanitize the amount string by replacing comma with a period
    private fun sanitizeAmount(amount: String): String {
        return amount.replace(",", ".") // Replace comma with a period
    }
}