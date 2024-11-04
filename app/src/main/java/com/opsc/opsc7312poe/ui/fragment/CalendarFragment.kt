package com.opsc.opsc7312.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.R
import com.opsc.opsc7312.api.viewmodel.TransactionViewModel
import com.opsc.opsc7312.databinding.FragmentCalendarBinding
import com.opsc.opsc7312.ui.adapter.TransactionAdapter
import com.opsc.opsc7312.ui.observer.TransactionObserver
import java.time.LocalDate
import com.github.mikephil.charting.charts.PieChart          // For creating a PieChart view
import com.github.mikephil.charting.data.PieData             // To set data for the PieChart
import com.github.mikephil.charting.data.PieDataSet          // To handle a dataset for the PieChart
import com.github.mikephil.charting.data.PieEntry            // To create individual entries in the PieChart
import com.github.mikephil.charting.utils.ColorTemplate      // For using predefined color templates
import com.opsc.opsc7312.api.local.LocalUser

import java.time.ZoneId

class CalendarFragment : Fragment() {

    // View binding for the fragment layout
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var pieChart: PieChart

    // Layouts for transactions and pie chart
    private lateinit var transactionsLayout: View
    private lateinit var pieChartLayout: View

    // Selected date, initialized to current date
    @RequiresApi(Build.VERSION_CODES.O)
    private var selectedDate: LocalDate = LocalDate.now()

    // ViewModel, adapter, observer, and RecyclerView for transactions
    private lateinit var viewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionObserver: TransactionObserver
    private lateinit var transactionRecyclerView: RecyclerView

    // User ID for fetching transactions
    private val userId = "your_user_id"

    // Called when the fragment's view is created
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the fragment layout and get the root view
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root

        // Inflate layouts for transactions and pie chart
        transactionsLayout = inflater.inflate(R.layout.layout_transactions, container, false)
        pieChartLayout = inflater.inflate(R.layout.layout_pie_chart, container, false)

        // Set click listener for the "Transactions" button
        binding.btnSpends.setOnClickListener {
            // Clear the content frame and add the transactions layout
            binding.contentFrame.removeAllViews()
            binding.contentFrame.addView(transactionsLayout)
            // Load transactions for the selected date
            loadTransactionsForDate(selectedDate)
        }

        // Set click listener for the "Categories" button
        binding.btnCategories.setOnClickListener {
            // Clear the content frame and add the pie chart layout
            binding.contentFrame.removeAllViews()
            binding.contentFrame.addView(pieChartLayout)
            // Implement pie chart loading logic here
        }

        // Set date change listener for the calendar view
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Update the selected date
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            // If transactions layout is visible, reload transactions
            if (binding.contentFrame.getChildAt(0) == transactionsLayout) {
                loadTransactionsForDate(selectedDate)
            } else if (binding.contentFrame.getChildAt(0) == pieChartLayout) {
                // Implement pie chart loading logic here
            }
        }

        // Initially, add the transactions layout to the content frame
        binding.contentFrame.addView(transactionsLayout)
        // Return the root view
        return view
    }

    // Called after the fragment's view is created
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the transaction adapter
        transactionAdapter = TransactionAdapter { transaction ->
            // Handle transaction item click if needed
        }

        pieChart = pieChartLayout.findViewById(R.id.pieChart) // Initialize pieChart

        // Find the RecyclerView in the transactions layout
        transactionRecyclerView = transactionsLayout.findViewById(R.id.transactionRecyclerView)
        // Set the adapter and layout manager for the RecyclerView
        transactionRecyclerView.adapter = transactionAdapter
        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the transaction observer
        transactionObserver = TransactionObserver(transactionAdapter)
        // Get an instance of the TransactionViewModel
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)

        // In your CalendarFragment's onViewCreated() method
        val localUser = LocalUser.getInstance(requireContext())
        val currentUser = localUser.getUser()

        val userId = currentUser?.id

        if (userId != null) {
            viewModel.getAllTransactions(userId)
        } else {
            // Handle case where user is not logged in (e.g., redirect to login)
            findNavController().navigate(R.id.action_calendarFragment_to_loginFragment)
        }

        // Load initial data for the current date
        loadTransactionsForDate(selectedDate)
        loadPieChartData(selectedDate) // Load initial pie chart data

        // Observe the transaction list LiveData for changes
        viewModel.transactionList.observe(viewLifecycleOwner) { transactions ->
            // Update the adapter with the new transactions
            transactionAdapter.updateTransactions(transactions)

            // Filter and display transactions for the selected date
            loadTransactionsForDate(selectedDate)
        }
    }

    // Called when the fragment's view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        // Clear the view binding
        _binding = null
    }

    // Loads transactions for the given date
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTransactionsForDate(selectedDate: LocalDate) {
        // Calculate the start and end of the selected day in milliseconds
        val startOfDay =
            selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay =
            selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        // Filter transactions based on the selected date
        val filteredTransactions = viewModel.transactionList.value?.filter { transaction ->
            // Add logging for debugging:
            // Log.d("CalendarFragment", "Transaction date: ${transaction.date}, startOfDay: $startOfDay, endOfDay: $endOfDay")
            transaction.date in startOfDay until endOfDay
        } ?: emptyList()

        // Update the adapter with the filtered transactions
        transactionAdapter.updateTransactions(filteredTransactions)

        // Load pie chart data after transactions are loaded
        loadPieChartData(selectedDate)
    }

    // Implement loadPieChartData here
    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadPieChartData(selectedDate: LocalDate) {
        val startOfDay = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfDay = selectedDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val filteredTransactions = viewModel.transactionList.value?.filter { transaction ->
            transaction.date in startOfDay until endOfDay
        } ?: emptyList()

        // Group transactions by category and calculate total amount for each category
        val categoryAmounts = filteredTransactions.groupingBy { it.category.name }
            .fold(0.0) { acc, transaction -> acc + transaction.amount }

        // Create PieEntry objects for the chart
        val entries = categoryAmounts.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        // Create PieDataSet and PieData objects
        val dataSet = PieDataSet(entries, "Transaction Categories")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data

        // Customize pie chart appearance (optional)
        pieChart.description.isEnabled = false // Hide description
        pieChart.centerText = "Transactions by Category" // Set center text
        pieChart.legend.isEnabled = true // Show legend

        pieChart.invalidate() // Refresh the chart
    }
}