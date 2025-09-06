package com.example.blinkit_admin.fragments

import android.app.AlertDialog
import android.content.Context
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
import com.example.blinkit_admin.databinding.ProgressDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import java.util.UUID

@AndroidEntryPoint
class AddProductFragment() : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private var imageUris: ArrayList<Uri> = arrayListOf()
    private val viewModal: AddProductViewModal by viewModels()
    val successfulUrls = mutableListOf<String>()

    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter: ArrayAdapter<String>
    val selectedImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUri ->

            val fiveImg = listOfUri.take(5)
            imageUris.clear()
            imageUris.addAll(fiveImg)
            binding.rcvSelectedProductImg.adapter = AdapterSelectedImage(imageUri = imageUris)
            uploadProductImages(imageUris)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentAddProductBinding.inflate(layoutInflater)

        categoryAdapter = ArrayAdapter(requireContext(), R.layout.show_list, mutableListOf<String>())
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
        try {
            onAddProductClicked()

        } catch (e: Exception) {
            Log.d("yash", e.toString())
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
                return@setOnClickListener
            }

            val category = binding.etCategory.text.toString().trim()
            if (category.isEmpty()) {
                binding.etCategory.error = "Invalid stock quantity"
                return@setOnClickListener
            }

            val type = binding.etType.text.toString().trim()
            if (type.isEmpty()) {
                binding.etType.error = "Invalid stock quantity"
                return@setOnClickListener
            }

            val stock = binding.ProductNoOfStock.text?.toString()?.toIntOrNull()
            if (stock == null) {
                binding.ProductNoOfStock.error = "Invalid stock quantity"
                return@setOnClickListener
            }

            val productName =
                if (viewModal.formData.isNotEmpty() && viewModal.formData[0] != null) {
                    viewModal.formData[0]?.value.toString().trim()
                } else {
                    Toast.makeText(requireContext(), "Product name is required", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }

            val img = successfulUrls
            val details = viewModal.formData


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
        }
    }

    private fun uploadProductImages(uris: List<Uri>) {
        Log.d("imgUpload","upload fun started")
        Log.d("imgUpload",uris.toString())

        var dialog : AlertDialog? = null
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.loginState.text =  "uploading images.."

        dialog = AlertDialog
            .Builder(context)
            .setView(progress.root)
            .setCancelable(false)
            .create()

        dialog?.show()

        lifecycleScope.launch {



            for ((index, uri) in uris.withIndex()) {

                try {
                    val imageBytes = withContext(Dispatchers.IO) {
                        uriToByteArray(requireContext(), uri)
                    }

                    if (imageBytes != null) {
                        val filePath = "public/${UUID.randomUUID()}.jpg"

                        val imageUrl =  viewModal.uploadProductImage(filePath,imageBytes)

                        Log.d("imgUpload",imageUrl)
                        // Get the public URL and add it to our success list.
                        successfulUrls.add(imageUrl)
                        progress.loginState.text  = "uploaded image $index/${uris.size}"

                    }
                } catch (e: Exception) {
                    Log.e("imgUpload",e.toString())
                }
                Log.e("imgUpload",successfulUrls.toString())
            }

            // 4. Update the UI after all uploads are finished.
            dialog?.hide()
            dialog = null


        }
    }


    private fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
//https://xpcvzteqrrgkblzrumaj.supabase.co/storage/v1/object/public/productImages/public/870acaf3-25d6-4ab7-a6fb-79e7e08773c6.jpg

//https://xpcvzteqrrgkblzrumaj.supabase.co/storage/v1/object/public/productImages/public/0c719863-0b17-410c-b2bb-9abb4adbfe58.jpg