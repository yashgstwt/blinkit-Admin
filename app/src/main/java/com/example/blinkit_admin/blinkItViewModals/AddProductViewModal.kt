package com.example.blinkit_admin.blinkItViewModals

import android.util.Log
import android.widget.AdapterView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.blinkit_admin.modals.dataClasses.CategoryItem
import com.example.blinkit_admin.modals.dataClasses.FormInputData
import com.example.blinkit_admin.modals.dataClasses.ProductFormInfoListItem
import com.example.blinkit_admin.modals.dataClasses.ProductTypeFormResponse
import com.example.blinkit_admin.modals.dataClasses.Type
import com.example.blinkit_admin.modals.dataClasses.productTable
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.client.content.LocalFileContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import javax.inject.Inject

@HiltViewModel
class AddProductViewModal @Inject constructor(
    private val supabase: SupabaseClient,
): ViewModel()  {

    var categoryList: List<CategoryItem>? = null
    var productTypeList : List<Type>? = null

    private var _productTypeForm : MutableStateFlow<List<ProductFormInfoListItem>>?= MutableStateFlow(emptyList())
    val productTypeForm  = _productTypeForm?.asStateFlow()

    private var _categoryDropdownList = MutableStateFlow<List<String>>(listOf("Loading..."))
    val categoryDropdownList =_categoryDropdownList.asStateFlow()

    private var _productTypeDropdownList = MutableStateFlow<List<String>>(listOf("Loading..."))
    val productTypeDropdownList = _productTypeDropdownList.asStateFlow()

    lateinit var formData: Array<FormInputData?>

    suspend fun fetchCategoryList() {
        try {
        //NOTE: In coroutine/launch block exceptions are not propagated upwards, hence it should be handled inside these block
             categoryList = supabase.from("category").select().decodeList<CategoryItem>()
            _categoryDropdownList.emit(categoryList?.map { it.name } as List<String>)
        }catch (e: Exception){
            Log.d("myResponse" , e.toString())
        }
    }

    suspend fun fetchProductTypeForm( productId:String ){

        try {
            val res : List<ProductTypeFormResponse> = supabase.from("product type form").select(Columns.list("product type form details")){
                filter {
                    eq("id", productId) // Filters for rows where "id" column matches productId
                }
            }.decodeList()

            _productTypeForm?.emit(res[0].productTypeFormDetails).also {
                Log.d("yash", "product form list fetchProductTypeFom() ------> ${res[0].productTypeFormDetails} ")

              formData = arrayOfNulls<FormInputData>(res[0].productTypeFormDetails.size  );
                Log.d("yash", "formData in  fetchProductTypeFom() ------> ${formData} ")

            }

        }catch (exception: Exception ){
            Log.d("yash" , exception.toString())
        }

    }


    fun onProductTypeSelected(parent: AdapterView<*>?, position: Int) {

        val selectedItemId = productTypeList?.get(position)?.id


        if (selectedItemId != null ) {

            try {
                viewModelScope.launch {
                    fetchProductTypeForm(selectedItemId)
                }.invokeOnCompletion {
                    Log.d("yash", "invoke on completion $productTypeForm")
                    Log.d("yash", " on productTypeSelected() formData :  $formData")
                }

            }catch (e: Exception){
                Log.d("yash", e.toString())
            }
        }
        formData = emptyArray()
    }

    fun onCategoryTypeSelected(parent: AdapterView<*>?, position: Int) {

            viewModelScope.launch {
                _productTypeDropdownList.emit(categoryList?.get(position)?.types?.map { it.Name } as List<String>)
            }
        productTypeList = categoryList?.get(position)?.types

    }

    suspend fun insertInputData(price:Int, productName:String, stock : Int, img: Array<String>?, details:Array<FormInputData?> , category:String , type:String){

        try{
            val formData : productTable = productTable(price = price, productName = productName, stock = stock , img = img , details = details, category = category ,type = type)

            val res = supabase.from("products_table").insert(formData)
            Log.d("yash",res.toString())

        }catch (e :Exception){
            Log.d("yash",e.toString())

        }

    }

}