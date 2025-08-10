package com.example.blinkit_admin.blinkItViewModals

import android.util.Log
import android.widget.AdapterView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkit_admin.modals.dataClasses.CategoryItem
import com.example.blinkit_admin.modals.dataClasses.ProductFormInfoListItem
import com.example.blinkit_admin.modals.dataClasses.ProductTypeFormResponse
import com.example.blinkit_admin.modals.dataClasses.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModal @Inject constructor(
    private val supabase: SupabaseClient,
): ViewModel()  {

    var categoryList: List<CategoryItem>? = null
    var productTypeList : List<Type>? = null
    var productTypeForm : List<ProductFormInfoListItem>? = null

    private var _categoryDropdownList = MutableStateFlow<List<String>>(listOf("Loading..."))
    val categoryDropdownList =_categoryDropdownList.asStateFlow()


    private var _productTypeDropdownList = MutableStateFlow<List<String>>(listOf("Loading..."))
    val productTypeDropdownList = _productTypeDropdownList.asStateFlow()
    
    suspend fun fetchCategoryList() {
        try {
        //NOTE: In coroutine/launch block exceptions are not propagated upwards, hence it should be handled inside these block
             categoryList = supabase.from("category").select().decodeList<CategoryItem>()
            _categoryDropdownList.emit(categoryList?.map { it.name } as List<String>)
//            Log.d("yash" , categoryList.toString())
        }catch (e: Exception){
            Log.d("myResponse" , e.toString())
        }
    }


    suspend fun fetchProductTypeForm( productId:String ){


        try {

            var res : List<ProductTypeFormResponse> = supabase.from("product type form").select(Columns.list("product type form details")){
                filter {

                    eq("id", productId) // Filters for rows where "id" column matches productId
                }
            }.decodeList()

            Log.d("yash", "product form list ------> ${res.get(0).productTypeFormDetails} ")

            productTypeForm = res[0].productTypeFormDetails
        }catch (exception: Exception ){
            Log.d("yash" , exception.toString())
        }

    }


    fun onProductTypeSelected(parent: AdapterView<*>?, position: Int) {
        val selectedItem = parent?.getItemAtPosition(position) as? String

        val selectedItemId = productTypeList?.get(position)?.id
        val selectedItemName = productTypeList?.get(position)?.Name


        if (selectedItemId != null ) {

            Log.d("yash", " onProductTypeSelected() ----- > Selected: $selectedItem at position: $position")
            Log.d("yash", " onProductTypeSelected() ----- > Selected product Id : $selectedItemId")
            Log.d("yash", " onProductTypeSelected() ----- > Selected product Name : $selectedItemName")

            try {
                viewModelScope.launch {
                    fetchProductTypeForm(selectedItemId)
                }.invokeOnCompletion {
                    Log.d("yash", productTypeForm.toString())
                }

            }catch (e: Exception){
                Log.d("yash", e.toString())
            }



        }
    }

    fun onCategoryTypeSelected(parent: AdapterView<*>?, position: Int) {

//        val selectedCategoryItem = parent?.getItemAtPosition(position) as? String
//        Log.d("yash", "Selected: $selectedCategoryItem at position: $position")
            viewModelScope.launch {
                _productTypeDropdownList.emit(categoryList?.get(position)?.types?.map { it.Name } as List<String>)
            }
        productTypeList = categoryList?.get(position)?.types
//       Log.d("yash", "onCategoryTypeSelected ----> Selected: $selectedCategoryItem at position: $position  and data ${_productTypeDropdownList.value}")

    }
}