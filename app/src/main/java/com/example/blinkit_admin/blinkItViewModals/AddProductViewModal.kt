package com.example.blinkit_admin.blinkItViewModals

import android.util.Log
import android.widget.AdapterView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkit_admin.modals.dataClasses.CategoryItem
import com.example.blinkit_admin.modals.dataClasses.Type
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
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


    private var _categoryDropdownList = MutableStateFlow<List<String>>(listOf("Loading..."))
    val categoryDropdownList =_categoryDropdownList.asStateFlow()


    private var _productTypeDropdownList = MutableStateFlow<List<String>>(listOf("Loading..."))
    val productTypeDropdownList = _productTypeDropdownList.asStateFlow()
    
    suspend fun fetchCategoryList() {
        try {
        //NOTE: In coroutine/launch block exceptions are not propagated upwards, hence it should be handled inside these block
             categoryList = supabase.from("category").select().decodeList<CategoryItem>()
          //  _categoryDropdownList.postValue(categoryList?.map { it.name })
            _categoryDropdownList.emit(categoryList?.map { it.name } as List<String>)
            Log.d("yash" , categoryList.toString())
        }catch (e: Exception){
            Log.d("myResponse" , e.toString())
        }
    }


    fun onProductTypeSelected(parent: AdapterView<*>?, position: Int) {
        val selectedItem = parent?.getItemAtPosition(position) as? String

        if (selectedItem != null) {
            productTypeList = categoryList?.get(position)?.types
            Log.d("yash", "Selected: $selectedItem at position: $position")
        }
    }

    fun onCategoryTypeSelected(parent: AdapterView<*>?, position: Int) {

        val selectedCategoryItem = parent?.getItemAtPosition(position) as? String
        Log.d("yash", "Selected: $selectedCategoryItem at position: $position")
            viewModelScope.launch {
                _productTypeDropdownList.emit(categoryList?.get(position)?.types?.map { it.Name } as List<String>)
            }
       Log.d("yash", "Selected: $selectedCategoryItem at position: $position  and data ${_productTypeDropdownList.value}")

    }
}