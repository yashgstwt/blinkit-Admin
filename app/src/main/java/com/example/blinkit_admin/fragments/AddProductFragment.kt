package com.example.blinkit_admin.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.blinkit_admin.adapters.AdapterSelectedImage
import com.example.blinkit_admin.R
import com.example.blinkit_admin.adapters.FormAdapter
import com.example.blinkit_admin.blinkItViewModals.AddProductViewModal
import com.example.blinkit_admin.databinding.FragmentAddProductBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddProductFragment () : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private var imageUris: ArrayList<Uri> = arrayListOf()
    private val viewModal: AddProductViewModal by viewModels()


    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter: ArrayAdapter<String>
    val selectedImage =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUri ->
            val fiveImg = listOfUri.take(5)
            imageUris.clear()
            imageUris.addAll(fiveImg)
            binding.rcvSelectedProductImg.adapter = AdapterSelectedImage(imageUri = imageUris)

        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentAddProductBinding.inflate(layoutInflater)

        categoryAdapter =
            ArrayAdapter(requireContext(), R.layout.show_list, mutableListOf<String>())
        binding.etCategory.setAdapter(categoryAdapter)
        try {
            binding.formFieldsRcv.adapter = FormAdapter(viewModal)
        } catch (e: Exception) {
            Log.d("yash", e.toString())
        }

        typeAdapter = ArrayAdapter(requireContext(), R.layout.show_list, mutableListOf<String>())
        binding.etType.setAdapter(typeAdapter)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.formViewModal = viewModal
        } catch (e: Exception) {
            Log.d("yash", e.toString())

        }
        observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModal.fetchCategoryList()
        }
        onImageSelectedClicked(selectedImage)
        try{
            onAddProductClicked()

        }catch (e : Exception){
            Log.d("yash",e.toString())
        }
    }

    private fun observeViewModel() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModal.categoryDropdownList.collect { categories ->
                    // This block executes whenever the StateFlow emits a new list
                    categoryAdapter.clear()
                    categoryAdapter.addAll(categories)
                    categoryAdapter.notifyDataSetChanged()
                    Log.d("yash", "Categories updated in Fragment: $categories")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModal.productTypeDropdownList.collect { productTypes ->
                typeAdapter.clear()
                typeAdapter.addAll(productTypes)
                typeAdapter.notifyDataSetChanged()
                Log.d("yash", " types updated in Fragment: $productTypes")

            }
        }
    }


    private fun onImageSelectedClicked(selectedImage: ActivityResultLauncher<String>) {
        binding.addProductImgBtn.setOnClickListener {
            selectedImage.launch("image/*")
        }
    }

    private fun onAddProductClicked() {

        binding.addProduct.setOnClickListener {
            val price = binding.ProductPrice.text?.toString()?.toIntOrNull()
            if (price == null) {
                binding.ProductPrice.error = "Invalid price"
                // potentially return or show a general error toast
                return@setOnClickListener
            }

            val category = binding.etCategory.text.toString().trim()
            if (category.isEmpty()) {
                binding.etCategory.error = "Invalid stock quantity"
                // potentially return or show a general error toast
                return@setOnClickListener
            }

            val type = binding.etType.text.toString().trim()
            if (type.isEmpty()) {
                binding.etType.error = "Invalid stock quantity"
                // potentially return or show a general error toast
                return@setOnClickListener
            }

            val stock = binding.ProductNoOfStock.text?.toString()?.toIntOrNull()
            if (stock == null) {
                binding.ProductNoOfStock.error = "Invalid stock quantity"
                // potentially return or show a general error toast
                return@setOnClickListener
            }

            val productName = if (viewModal.formData.isNotEmpty() && viewModal.formData[0] != null) {
                viewModal.formData[0]?.value.toString().trim()
            } else {
                Toast.makeText(requireContext(), "Product name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // or some other error handling
            }

            val img = arrayOf("https://unsplash.com/photos/a-piece-of-cake-next-to-an-apple-on-a-pink-surface-BBx8QqVfLPU")
            val details = viewModal.formData


//            if (isPriceValid(price.toString()) && isCategoryValid(category) && isTypeValid(type) && isStockValid(stock.toString())) {

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                        viewModal.insertInputData(
                            price = price,
                            category = category,
                            type = type,
                            stock = stock,
                            productName = productName,
                            img = img,
                            details = details
                        )
                        Log.d("yash", "13333")
                        } catch (e: Exception) {
                            Log.d("yash", e.toString())
                            Toast.makeText(
                                requireContext(),
                                "Insertion Failed! Please Try Again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.invokeOnCompletion { throwable ->

                    lifecycleScope.launch(Dispatchers.Main) {
                            if (throwable == null) {
                                Toast.makeText(
                                    requireContext(),
                                    "Successfully inserted product details",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Log.e("yash", "Error inserting product", throwable)
                                Toast.makeText(
                                    requireContext(),
                                    "Insertion Failed! Please Try Again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
//            }else{
//                Log.d("yash", "fields cannot be empty")
//                Toast.makeText(
//                    requireContext(),
//                    "Insertion Failed! Please Try Again",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
            Log.d("yash", viewModal.formData.contentToString())
        }
    }

    private fun isPriceValid(value: String): Boolean {

        if (value.isEmpty()) {
            binding.ProductPrice.error = "This field is required."
            return false
        } else {
            binding.ProductPrice.error = null
            return true
        }
    }
    private fun isCategoryValid(value: String): Boolean {

        if (value.isEmpty()) {
            binding.etCategory.error = "This field is required."
            return false
        } else {
            binding.etCategory.error = null
            return true
        }
    }
    private fun isNameValid(value: String): Boolean {

        if (value.isEmpty()) {
            binding
            //ProductPrice.error = "This field is required."
            return false
        } else {
            binding.ProductPrice.error = null
            return true
        }
    }

    private fun isStockValid(value: String): Boolean {

        if (value.isEmpty()) {
            binding.ProductNoOfStock.error = "This field is required."
            return false
        } else {
            binding.ProductNoOfStock.error = null
            return true
        }
    }

    private fun isTypeValid(value: String): Boolean {

        if (value.isEmpty()) {
            binding.etType.error = "This field is required."
            return false
        } else {
            binding.etType.error = null
            return true
        }
    }
}