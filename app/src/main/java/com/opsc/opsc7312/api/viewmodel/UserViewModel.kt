package com.opsc.opsc7312.api.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312.api.data.User
import com.opsc.opsc7312.api.retrofitclient
import com.opsc.opsc7312.api.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {
    // api: Retrofit client for making API requests related to authentication.
    private val api: UserService = retrofitclient.createService<UserService>()

    // status: LiveData that holds the status of API requests (true for success, false for failure).
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // message: LiveData that contains success or error messages based on API responses.
    val message: MutableLiveData<String> = MutableLiveData()

    // userData: LiveData holding the user information retrieved during registration or login.
    val userData: MutableLiveData<User> = MutableLiveData()

    // Function to handle user registration.
    fun register(user: User) {
        api.register(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    handleResponse(response, "User registered successfully")
                } else if (response.code() == 409) { // Handle 409 Conflict (user exists)
                    status.postValue(false)
                    message.postValue("User already exists")
                } else {
                    handleFailure(Throwable("Registration failed with code: ${response.code()}"))
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                handleFailure(t)
            }
        })
    }

    // Function to handle user login.
    fun login(user: User) {
        api.login(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                handleResponse(response, "User logged in successfully")
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                handleFailure(t)
            }
        })
    }

    // Function to handle Single Sign-On (SSO) registration.
    fun registerWithSSO(user: User) {
        api.registerWithSSO(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                handleResponse(response, "User registered successfully")
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                handleFailure(t)
            }
        })
    }

    // Function to handle Single Sign-On (SSO) login.
    fun loginWithSSO(user: User) {
        api.loginWithSSO(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                handleResponse(response, "User logged in with SSO successfully")
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                handleFailure(t)
            }
        })
    }

    // Function to update user information.
    fun updateUser(id: String, user: User) {
        val call = api.updateUser(id, user)
        Log.d("UserViewModel", "Request URL: ${call.request().url}")

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    updatedUser?.let {
                        status.postValue(true)
                        message.postValue("User email and username updated: $it")
                        Log.d("UserViewModel", "User email and username updated: $it")
                    }
                } else {
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("UserViewModel", "Request failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                handleFailure(t)
            }
        })
    }

    // Common method to handle successful responses
    private fun handleResponse(response: Response<User>, successMessage: String) {
        if (response.isSuccessful) {
            response.body()?.let {
                status.postValue(true)
                message.postValue(successMessage)
                userData.postValue(it)
                Log.d("UserViewModel", successMessage + ": $it")
            }
        } else {
            status.postValue(false)
            message.postValue("Request failed with code: ${response.code()}: ${response.errorBody()?.string()}")
            Log.e("UserViewModel", "Request failed with code: ${response.code()}")
        }
    }

    // Common method to handle failures
    private fun handleFailure(t: Throwable) {
        Log.e("UserViewModel", "Error: ${t.message ?: "Unknown error"}")
        status.postValue(false)
        message.postValue(t.message ?: "Unknown error")
    }
}
