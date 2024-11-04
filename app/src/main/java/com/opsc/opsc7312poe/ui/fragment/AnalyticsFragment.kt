package com.opsc.opsc7312poe.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.opsc.opsc7312poe.R
import com.opsc.opsc7312poe.api.data.Transaction
import com.opsc.opsc7312poe.api.local.LocalUser
import com.opsc.opsc7312poe.api.local.db.databasehelper.CategoryDatabaseHelper
import com.opsc.opsc7312poe.api.local.db.databasehelper.TransactionDatabaseHelper
import com.opsc.opsc7312poe.databinding.FragmentAnalyticsBinding
import com.opsc.opsc7312poe.databinding.FragmentTransactionDetailsBinding
import com.opsc.opsc7312poe.databinding.FragmentTransactionsBinding
import com.opsc.opsc7312poe.ui.activity.LoginActivity
import java.util.Calendar

class AnalyticsFragment : Fragment() {
    private lateinit var binding: FragmentAnalyticsBinding
    private lateinit var transactionDatabaseHelper: TransactionDatabaseHelper
    private lateinit var cateDatabaseHelper: CategoryDatabaseHelper
    private lateinit var localUser: LocalUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalyticsBinding.inflate(layoutInflater)

        transactionDatabaseHelper = TransactionDatabaseHelper(requireContext())

        cateDatabaseHelper = CategoryDatabaseHelper(requireContext())

        localUser = LocalUser.getInstance(requireContext())

        val currentUser = localUser.getUser()

        // If the token is valid, observe the category data through the ViewModel.
        if (currentUser != null) {
            getTransactions(currentUser.id)

        } else {
            // Handle the scenario where the token is null (e.g., log an error or show a message).
            startActivity(Intent(requireContext(), LoginActivity::class.java)) // Restart the MainActivity
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getTransactions(userId: String){

        try {
            val transactions = transactionDatabaseHelper.getAllTransactions(userId)

            for (transaction in transactions){
                transaction.category = cateDatabaseHelper.getCategory(transaction.categoryid)!!
            }
            Log.d("transactions", "transactions: $transactions")
            // Calculate monthly income and expense totals
            val monthlyTotals = calculateMonthlyTotals(transactions)
            setupBarChart(monthlyTotals)

        } catch (e: Exception) {
            Log.e("DatabaseError", "Error inserting transaction", e)
        }
    }

    private fun calculateMonthlyTotals(transactions: List<Transaction>): Map<String, Pair<Double, Double>> {
        val calendar = Calendar.getInstance()

        // Get the current date and go back 6 months
        calendar.add(Calendar.MONTH, -5)
        val startDate = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 5) // Reset to current date
        val endDate = calendar.timeInMillis

        // Filter and accumulate totals
        val monthlyTotals = mutableMapOf<String, Pair<Double, Double>>()
        for (transaction in transactions.filter { it.date in startDate..endDate }) {
            calendar.timeInMillis = transaction.date
            val monthYear = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}"

            val (income, expense) = monthlyTotals.getOrDefault(monthYear, 0.0 to 0.0)
            if (transaction.transactionType.equals("income", ignoreCase = true)) {
                monthlyTotals[monthYear] = income + transaction.amount to expense
            } else if (transaction.transactionType.equals("expenses", ignoreCase = true)) {
                monthlyTotals[monthYear] = income to expense + transaction.amount
            } else {
                monthlyTotals[monthYear] = income to expense + transaction.amount

            }
        }

        return monthlyTotals
    }

    private fun setupBarChart(monthlyTotals: Map<String, Pair<Double, Double>>) {
        val incomeEntries = ArrayList<BarEntry>()
        val expenseEntries = ArrayList<BarEntry>()

        var xIndex = 0f
        monthlyTotals.toSortedMap().forEach { (monthYear, totals) ->
            incomeEntries.add(BarEntry(xIndex, totals.first.toFloat()))
            expenseEntries.add(BarEntry(xIndex, totals.second.toFloat()))
            xIndex++
        }

        val incomeDataSet = BarDataSet(incomeEntries, getString(R.string.income)).apply {
            color = ColorTemplate.COLORFUL_COLORS[1]
        }
        val expenseDataSet = BarDataSet(expenseEntries, getString(R.string.expenses)).apply {
            color = ColorTemplate.COLORFUL_COLORS[3]
        }

        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.4f
        }

        binding.incomeExpenseBarChart.apply {
            data = barData
            setFitBars(true)
            description.isEnabled = false
            animateY(1000)
            invalidate() // Refresh the chart
        }
    }
}