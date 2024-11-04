package com.opsc.opsc7312poe.api.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.opsc.opsc7312poe.api.data.User
import com.opsc.opsc7312poe.api.retrofitclient
import com.opsc.opsc7312poe.api.service.UserService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel: ViewModel() {
    // api: Retrofit client for making API requests related to authentication.
    var api: UserService = retrofitclient.createService<UserService>()

    // status: LiveData that holds the status of API requests (true for success, false for failure).
    val status: MutableLiveData<Boolean> = MutableLiveData()

    // message: LiveData that contains success or error messages based on API responses.
    val message: MutableLiveData<String> = MutableLiveData()

    // userData: LiveData holding the user information retrieved during registration or login.
    val userData: MutableLiveData<User> = MutableLiveData()

    // Function to handle user registration.
    // Takes a User object, makes a registration API call, and updates LiveData based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun register(user: User) {
        api.register(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If registration is successful, retrieve the created user and update LiveData.
                    val createdUser = response.body()
                    createdUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User registered successfully")  // Post success message.
                        userData.postValue(it)  // Post the created user data.
                        Log.d("MainActivity", "User created: $it")
                    }
                } else {
                    // Handle failure when the response is not successful (e.g., non-2xx status code).
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}: ${response.body()?.message}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }

    // Function to handle user login.
    // Takes a User object, makes a login API call, and updates LiveData based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun login(user: User) {
        api.login(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If login is successful, retrieve the logged-in user and update LiveData.
                    val loggedInUser = response.body()
                    loggedInUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User logged in successfully")  // Post success message.
                        userData.postValue(it)  // Post the logged-in user data.
                        Log.d("MainActivity", "User logged in: $it")
                    }
                } else {
                    // Handle failure when the response is not successful.
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.message}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                //Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }


    // Function to handle Single Sign-On (SSO) registration.
    // Takes a User object, makes a registration API call with SSO, and updates LiveData based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun registerWithSSO(user: User) {
        api.registerWithSSO(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If SSO registration is successful, retrieve the created user.
                    val createdUser = response.body()
                    createdUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User registered with SSO successfully")  // Post success message.
                        Log.d("MainActivity", "User created with SSO: $it")
                    }
                } else {
                    // Handle failure during SSO registration.
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}: ${response.body()?.message}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.message}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }

    // Function to handle Single Sign-On (SSO) login.
    // Takes a User object, makes a login API call with SSO, and updates LiveData based on the response.

    // This method was adapted from medium
    // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
    // Megha Verma
    // https://medium.com/@meghaverma12
    fun loginWithSSO(user: User) {
        api.loginWithSSO(user).enqueue(object : Callback<User> {

            // Called when the API call receives a response.
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // If SSO login is successful, retrieve the logged-in user.
                    val loggedInUser = response.body()
                    loggedInUser?.let {
                        status.postValue(true)  // Update status to indicate success.
                        message.postValue("User logged in with SSO successfully")  // Post success message.
                        userData.postValue(it)  // Post the logged-in user data.
                        Log.d("MainActivity", "User logged in with SSO: $it")
                    }
                } else {
                    // Handle failure during SSO login.
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}: ${response.body()?.message}")
                }
            }

            // Called when the API call fails due to network issues or other errors.
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)  // Update status to indicate failure.
                message.postValue(t.message)  // Post failure message.
            }
        })
    }


    fun updateUser(id: String, user: User) {
        // This method was adapted from medium
        // https://medium.com/quick-code/working-with-restful-apis-in-android-retrofit-volley-okhttp-eb8d3ec71e06
        // Megha Verma
        // https://medium.com/@meghaverma12
        // Prepare the authorization token

        // Initiate the API call to update the user's email and username, passing the user object
        val call = api.updateUser(id, user)

        // Log the request URL for debugging purposes
        val url = call.request().url.toString()
        Log.d("MainActivity", "Request URL: $url")

        // Enqueue the request to execute it asynchronously
        call.enqueue(object : Callback<User> {
            // Handle the server's response when the request succeeds
            override fun onResponse(call: Call<User>, response: Response<User>) {
                // If the response indicates success, update the user information in LiveData
                if (response.isSuccessful) {
                    val updatedUser = response.body()
                    updatedUser?.let {
                        // Update the status to true to indicate success and post the updated user data
                        status.postValue(true)
                        message.postValue("User email and username updated: $it")
                        Log.d("MainActivity", "User email and username updated: $it")
                    }
                } else {
                    // If the request fails, log the error code and update the LiveData accordingly
                    status.postValue(false)
                    message.postValue("Request failed with code: ${response.code()}")
                    Log.e("MainActivity", "Request failed with code: ${response.code()}")
                }
            }

            // Handle cases where the request fails due to network errors or other issues
            override fun onFailure(call: Call<User>, t: Throwable) {
                // Log the error and update the LiveData to reflect the failure
                Log.e("MainActivity", "Error: ${t.message}")
                status.postValue(false)
                message.postValue(t.message)
            }
        })
    }

}