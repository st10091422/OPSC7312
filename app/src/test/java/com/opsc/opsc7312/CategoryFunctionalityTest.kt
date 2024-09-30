package com.opsc.opsc7312

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.opsc.opsc7312.api.data.Category
import com.opsc.opsc7312.api.service.CategoryService
import com.opsc.opsc7312.api.viewmodel.CategoryViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFunctionalityTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var apiService: CategoryService

    @Mock
    lateinit var mockApi: CategoryService

    @Mock
    lateinit var mockCall: Call<List<Category>>

    @Mock
    lateinit var mockCall2: Call<Category>

    @Mock
    lateinit var observerStatus: Observer<Boolean>

    @Mock
    lateinit var observerMessage: Observer<String>

    @Mock
    lateinit var observerCategoryList: Observer<List<Category>>



    private lateinit var categoryViewModel: CategoryViewModel



    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        categoryViewModel = CategoryViewModel()
        categoryViewModel.api = apiService // Inject the mocked API service


        // Observe LiveData
        categoryViewModel.status.observeForever(observerStatus)
        categoryViewModel.message.observeForever(observerMessage)
        categoryViewModel.categoryList.observeForever(observerCategoryList)
    }

    @Test
    fun testGetAllCategoriesSuccess() {
        // Given
        val categories = listOf(
            Category("1", "Category1"),
            Category("2", "Category2")
        )
        `when`(apiService.getCategories("userId123")).thenReturn(mockCall)

        // Attach observers
        categoryViewModel.status.observeForever(observerStatus)
        categoryViewModel.message.observeForever(observerMessage)
        categoryViewModel.categoryList.observeForever(observerCategoryList)

        // Simulate the successful response
        doAnswer { invocation ->
            val callback: Callback<List<Category>> = invocation.getArgument(0)
            callback.onResponse(mockCall, Response.success(categories))
            null
        }.`when`(mockCall).enqueue(any())

        // When
        categoryViewModel.getAllCategories("userId123")

        // Then
        verify(observerStatus).onChanged(true)
        verify(observerMessage).onChanged("Categories retrieved")
        verify(observerCategoryList).onChanged(categories)
    }


}