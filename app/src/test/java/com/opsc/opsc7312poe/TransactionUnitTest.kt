package com.opsc.opsc7312poe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.opsc.opsc7312poe.api.data.Category
import com.opsc.opsc7312poe.api.data.Transaction
import com.opsc.opsc7312poe.api.service.CategoryService
import com.opsc.opsc7312poe.api.service.TransactionService
import com.opsc.opsc7312poe.api.viewmodel.TransactionViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionUnitTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: TransactionService

    @Mock
    lateinit var mockApi: CategoryService

    @Mock
    lateinit var mockCall: Call<List<Transaction>>

    @Mock
    lateinit var mockCall2: Call<Category>

    @Mock
    lateinit var observerStatus: Observer<Boolean>

    @Mock
    lateinit var observerMessage: Observer<String>

    @Mock
    lateinit var observerCategoryList: Observer<List<Transaction>>



    private lateinit var transactionViewModel: TransactionViewModel



    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        transactionViewModel = TransactionViewModel()
        transactionViewModel.api = apiService // Inject the mocked API service


        // Observe LiveData
        transactionViewModel.status.observeForever(observerStatus)
        transactionViewModel.message.observeForever(observerMessage)
        transactionViewModel.transactionList.observeForever(observerCategoryList)
    }

    @Test
    fun testGetAllTransactionsSuccess() {
        // Given
        val transaction = listOf(
            Transaction("1", "Category1"),
            Transaction("2", "Category2")
        )
        `when`(apiService.getTransactions("userId123")).thenReturn(mockCall)

        // Attach observers
        transactionViewModel.status.observeForever(observerStatus)
        transactionViewModel.message.observeForever(observerMessage)
        transactionViewModel.transactionList.observeForever(observerCategoryList)

        // Simulate the successful response
        doAnswer { invocation ->
            val callback: Callback<List<Transaction>> = invocation.getArgument(0)
            callback.onResponse(mockCall, Response.success(transaction))
            null
        }.`when`(mockCall).enqueue(any())

        // When
        transactionViewModel.getAllTransactions("userId123")

        // Then
        verify(observerStatus).onChanged(true)
        verify(observerMessage).onChanged("Transactions retrieved")
        verify(observerCategoryList).onChanged(transaction)
    }

}