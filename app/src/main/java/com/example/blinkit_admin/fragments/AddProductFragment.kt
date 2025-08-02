package com.example.blinkit_admin.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.blinkit_admin.adapters.AdapterSelectedImage
import com.example.blinkit_admin.R
import com.example.blinkit_admin.utils.Constants
import com.example.blinkit_admin.utils.Utils
import com.example.blinkit_admin.blinkItViewModals.AddProductViewModal
import com.example.blinkit_admin.databinding.FragmentAddProductBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddProductFragment : Fragment() {
    private lateinit var binding : FragmentAddProductBinding
    private var imageUris:ArrayList<Uri> = arrayListOf()
    private val viewModal: AddProductViewModal by  viewModels()

    init {
        Log.d("yash", "init block")
    }

    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var typeAdapter : ArrayAdapter<String>


    // val category = ArrayAdapter(requireContext(), R.layout.show_list, mutableListOf<String>())
   val selectedImage = registerForActivityResult(ActivityResultContracts.GetMultipleContents()){ listOfUri ->
       val fiveImg =  listOfUri.take(5)
       imageUris.clear()

       imageUris.addAll(fiveImg)
       binding.rcvSelectedProductImg.adapter = AdapterSelectedImage(imageUri = imageUris)

   }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ):View? {

        binding = FragmentAddProductBinding.inflate(layoutInflater)

        categoryAdapter = ArrayAdapter(requireContext(), R.layout.show_list, mutableListOf<String>())
        binding.etCategory.setAdapter(categoryAdapter)


        typeAdapter= ArrayAdapter(requireContext(), R.layout.show_list, mutableListOf<String>())
        binding.etType.setAdapter(typeAdapter)

        Log.d("yash", "onCreateView")

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.formViewModal = viewModal
        }catch (e: Exception){
            Log.d("yash", e.toString())

        }
        observeViewModel()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModal.fetchCategoryList()
        }
        setAutoCompleteTextViews()
        onImageSelectedClicked(selectedImage)
        onAddProductClicked()
    }

    private fun observeViewModel() {
        // In your AddProductFragment's observeViewModel() or onViewCreated()

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




    private fun setAutoCompleteTextViews(){
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProduct)

        binding.apply {
            etType.onItemClickListener
            etUnit.setAdapter(units)
        }
    }


    private fun onImageSelectedClicked(selectedImage: ActivityResultLauncher<String>) {
        binding.addProductImgBtn.setOnClickListener {
            selectedImage.launch("image/*")
        }
    }


//    private  fun setupFormFields(){
//        binding.formFieldsRcv.adapter = FormAdapter(viewModal)
//
//    }




    private fun onAddProductClicked() {

        binding.addProduct.setOnClickListener{


            Utils.showDialog(requireContext() , "Uploading the product")

//            val productName = binding.ProductName.text.toString()
//            val quantity = binding.etProductQuantity.text.toString()
//            val unit = binding.etUnit.text.toString()
//            val price = binding.ProductPrice.text.toString()
//            val category = binding.etCategory.toString()
//            val type = binding.etType.toString()
//            if (productName.isEmpty() || quantity.isEmpty() || unit.isEmpty() || price.isEmpty() || category.isEmpty() || type.isEmpty() ) {
//                Utils.hideDialog()
//                Utils.showDialog(requireContext() , "Fields Cannot be Empty")
//                lifecycleScope.launch {
//                    delay(5000)
//                    Utils.hideDialog()
//                }
//            }else if (imageUris.isEmpty()){
//                Utils.showDialog(requireContext() , "Please upload some Images ")
//                lifecycleScope.launch {
//                    delay(5000)
//                    Utils.hideDialog()
//                }
//            }else{
//
//
//            }

        }
    }
}