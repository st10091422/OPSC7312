package com.opsc.opsc7312.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.opsc.opsc7312.MainActivity
import com.opsc.opsc7312.R
import com.opsc.opsc7312.api.data.Category
import com.opsc.opsc7312.api.local.LocalUser
import com.opsc.opsc7312.api.viewmodel.CategoryViewModel
import com.opsc.opsc7312.databinding.ActivityRegisterBinding
import com.opsc.opsc7312.databinding.FragmentCategoriesBinding
import com.opsc.opsc7312.ui.adapter.CategoryAdapter
import com.opsc.opsc7312.ui.adapter.CategoryIconAdapter
import com.opsc.opsc7312.ui.observer.CategoriesObserver

class CategoriesFragment : Fragment() {
    private lateinit var binding: FragmentCategoriesBinding

    private lateinit var categoryAdapter: CategoryAdapter

    // ViewModel responsible for managing and processing the category data.
    private lateinit var categoryViewModel: CategoryViewModel

    private lateinit var localUser: LocalUser

    private lateinit var iconRecyclerView: RecyclerView
    private lateinit var iconPickerDialog: AlertDialog

    private lateinit var iconAdapter: CategoryIconAdapter

    private lateinit var updateIconName: TextView

    private lateinit var createIconName: TextView

    private lateinit var updateIconImageView: ImageView

    private lateinit var createIconImageView: ImageView



    private var iconName: String = "Choose Icon"


    val CategoryIcons = mapOf(
        "groceries" to R.drawable.baseline_shopping_cart_24,
        "education" to R.drawable.baseline_school_24,
        "travel" to R.drawable.baseline_airplanemode_active_24,
        "transportation" to R.drawable.baseline_directions_bus_24,
        "healthcare" to R.drawable.baseline_medical_services_24,
        "gift" to R.drawable.baseline_card_giftcard_24
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(layoutInflater)


        // Get the CategoryController ViewModel for interacting with category data.
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        localUser = LocalUser.getInstance(requireContext())

        // Set up the category adapter with a click listener for category items.
        // It handles both clicking on an existing category and the "create" button.
        categoryAdapter = CategoryAdapter { category ->
            if (category.isCreateButton) {
                // Redirect to the create category screen when the "create" button is clicked.
                showCreateCategory()
            } else {
                // Redirect to the category details screen when an existing category is clicked.
                showCategoryDetails(category)
            }
        }

        val icons = ArrayList(CategoryIcons.values)

        iconAdapter = CategoryIconAdapter(icons) { selectedIcon ->
            // Set the selected icon to the ImageView and update UI
            iconName = CategoryIcons.entries.find { it.value == selectedIcon }?.key ?: "Unknown Icon"

            if (::updateIconName.isInitialized) {
                updateIconName.setText(iconName)
                updateIconImageView.setImageResource(selectedIcon)
            }

            if (::createIconName.isInitialized) {
                createIconName.setText(iconName)
                createIconImageView.setImageResource(selectedIcon)
            }
            // Dismiss the icon picker dialog after selection
            iconPickerDialog.dismiss()
        }

        binding.categoryRecycleView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Ensure the RecyclerView has a fixed size for better performance.
        binding.categoryRecycleView.setHasFixedSize(true)

        // Set the adapter for the RecyclerView to display the categories.
        binding.categoryRecycleView.adapter = categoryAdapter


        val currentUser = localUser.getUser()



        // If the token is valid, observe the category data through the ViewModel.
        if (currentUser != null) {
            getAllCategories(currentUser.id)
        } else {
            startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
        }

        return binding.root
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
                Toast.makeText(requireContext(), "Categories retrieved!", Toast.LENGTH_SHORT).show() // Show logout message


            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT).show() // Show logout message

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
                Toast.makeText(requireContext(), "Connection Timeout!", Toast.LENGTH_SHORT).show() // Show logout message
                categoryViewModel.getAllCategories(id)
            }
        }

        // Observe the category list and update the UI accordingly.
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel
        categoryViewModel.categoryList.observe(viewLifecycleOwner, CategoriesObserver(categoryAdapter))

        // Initial API call to fetch all categories from the server.
        categoryViewModel.getAllCategories(id)
    }

    private fun addCategory(category: Category) {

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
                Toast.makeText(requireContext(), "Category created!", Toast.LENGTH_SHORT).show() // Show logout message
                changeCurrentFragment(this)

            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT).show() // Show logout message

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
                Toast.makeText(requireContext(), "Connection Timeout!", Toast.LENGTH_SHORT).show() // Show logout message
                categoryViewModel.createCategory(category)
            }
        }

        // Observe the category list and update the UI accordingly.
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel

        // Initial API call to fetch all categories from the server.
        categoryViewModel.createCategory(category)
    }

    private fun updateCategory(id: String, category: Category) {

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
                Toast.makeText(requireContext(), "Categories updated!", Toast.LENGTH_SHORT).show() // Show logout message
                changeCurrentFragment(this)

            } else {
                // Update the progress dialog for unsuccessful registration
                Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_SHORT).show() // Show logout message

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
                Toast.makeText(requireContext(), "Connection Timeout!", Toast.LENGTH_SHORT).show() // Show logout message
                categoryViewModel.createCategory(category)
            }
        }

        // Observe the category list and update the UI accordingly.
        // This observer implementation was adapted from stackoverflow
        // https://stackoverflow.com/questions/47025233/android-lifecycle-library-cannot-add-the-same-observer-with-different-lifecycle
        // Kevin Robatel
        // https://stackoverflow.com/users/244702/kevin-robatel

        // Initial API call to fetch all categories from the server.
        categoryViewModel.updateCategory(id, category)
    }


    // Redirects the user to the Create Category screen.
    private fun redirectToCreate() {
        // Create a new instance of CreateCategoryFragment.
        val createCategoryFragment = CreateCategoryFragment()

        // Navigate to the Create Category screen.
        changeCurrentFragment(createCategoryFragment)
    }



    private fun redirectToDetails(category: Category) {
        // Create a new instance of UpdateCategoryFragment and pass the selected category data.
        val categoryDetailsFragment = CategoryDetailsFragment()
        val bundle = Bundle()
        bundle.putString("id", category.id)
        bundle.putString("icon", category.icon)
        bundle.putString("name", category.name)

        categoryDetailsFragment.arguments = bundle

        // Navigate to the Category Details screen.
        changeCurrentFragment(categoryDetailsFragment)
    }

    private fun showIconPickerDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_picker_dialog, null)

        // Initialize RecyclerView for displaying icons
        iconRecyclerView = dialogView.findViewById(R.id.recyclerView)
        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // Set span count for grid layout
        iconRecyclerView.adapter = iconAdapter // Set the icon adapter

        // Create and show the icon picker dialog
        iconPickerDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        iconPickerDialog.show() // Show the dialog
    }

    private fun showCreateCategory() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.create_category_dialog, null)

        val categoryName:EditText = dialogView.findViewById(R.id.etCategoryName)
        createIconImageView = dialogView.findViewById(R.id.iconImageView)
        val saveBtn: AppCompatButton = dialogView.findViewById(R.id.btnSave)
        val cancelBtn: AppCompatButton = dialogView.findViewById(R.id.btnCancel)
        createIconName = dialogView.findViewById(R.id.iconName)
        val iconPicker: LinearLayout = dialogView.findViewById(R.id.iconPicker)


        // Create and show the icon picker dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }

        iconPicker.setOnClickListener {
            showIconPickerDialog()
        }

        saveBtn.setOnClickListener{
            val selectedIcon = iconAdapter.getSelectedItem()
            if(categoryName.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter a category name!", Toast.LENGTH_SHORT).show() // Show logout message
            }

            if(selectedIcon == null){
                Toast.makeText(requireContext(), "Select icon!", Toast.LENGTH_SHORT).show() // Show logout message
            }
            if(categoryName.text.toString().isNotEmpty() && selectedIcon != null){
                val currentUser = localUser.getUser()

                // If the token is valid, observe the category data through the ViewModel.
                if (currentUser != null) {
                    val newCategory = Category(name = categoryName.text.toString(), icon = CategoryIcons.entries.find { it.value == selectedIcon }?.key ?: "Unknown Icon", userid = currentUser.id)
                    addCategory(newCategory)
                } else {
                    // Handle the scenario where the token is null (e.g., log an error or show a message).
                    startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
                }
            }
        }

        dialogBuilder.show()// Show the dialog
    }

    private fun showCategoryDetails(category: Category) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.update_category_dialog, null)

        val categoryName:EditText = dialogView.findViewById(R.id.etCategoryName)
        updateIconImageView = dialogView.findViewById(R.id.iconImageView)
        val saveBtn: AppCompatButton = dialogView.findViewById(R.id.btnSave)
        val cancelBtn: AppCompatButton = dialogView.findViewById(R.id.btnCancel)
        updateIconName = dialogView.findViewById(R.id.iconName)
        val iconPicker: LinearLayout = dialogView.findViewById(R.id.iconPicker)

        // Create and show the icon picker dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        CategoryIcons[category.icon]?.let { updateIconImageView.setImageResource(it) }
        categoryName.setText(category.name)

        cancelBtn.setOnClickListener {
            dialogBuilder.dismiss()
        }

        iconPicker.setOnClickListener {
            showIconPickerDialog()
        }

        saveBtn.setOnClickListener{
            val selectedIcon = iconAdapter.getSelectedItem()
            if(categoryName.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Enter a category name!", Toast.LENGTH_SHORT).show() // Show logout message
            }

            if(selectedIcon == null){
                Toast.makeText(requireContext(), "Select icon!", Toast.LENGTH_SHORT).show() // Show logout message
            }
            if(categoryName.text.toString().isNotEmpty() && selectedIcon != null){
                val currentUser = localUser.getUser()

                // If the token is valid, observe the category data through the ViewModel.
                if (currentUser != null) {
                    val updatedCategory = Category(id = category.id, name = category.name, icon = CategoryIcons.entries.find { it.value == selectedIcon }?.key ?: "Unknown Icon", userid = category.userid)
                    updateCategory(category.id, updatedCategory)
                } else {
                    // Handle the scenario where the token is null (e.g., log an error or show a message).
                    startActivity(Intent(requireContext(), MainActivity::class.java)) // Restart the MainActivity
                }
            }

        }

        dialogBuilder.show()// Show the dialog
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