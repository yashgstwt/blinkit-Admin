package com.example.blinkit_admin.adapters

import android.annotation.SuppressLint
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkit_admin.modals.dataClasses.ProductFormInfoListItem
import com.example.blinkit_admin.R
import com.example.blinkit_admin.blinkItViewModals.AddProductViewModal
import com.example.blinkit_admin.databinding.OptionsFieldBinding
import com.example.blinkit_admin.databinding.TextFieldBinding
import com.example.blinkit_admin.databinding.TextWithOptionsFieldBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormAdapter(viewModal: AddProductViewModal) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var formList : List<ProductFormInfoListItem> = emptyList()


    init {
        CoroutineScope(Dispatchers.IO).launch {
            viewModal.productTypeForm?.collect {
                formList = it
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {


        return when (viewType){

            0 -> {

                val binding = TextFieldBinding.inflate(LayoutInflater.from(parent.context), parent , false)
                InputTextViewHolder(binding)
            }

            1 -> {
                val binding = OptionsFieldBinding.inflate(LayoutInflater.from(parent.context), parent , false)

                OptionsViewHolder(binding)
            }

            2 -> {

                val binding = TextWithOptionsFieldBinding.inflate(LayoutInflater.from(parent.context), parent , false)

                InputTextWithOptionsViewHolder(binding)
            }

            else -> {
                val binding = TextFieldBinding.inflate(LayoutInflater.from(parent.context), parent , false)

                InputTextViewHolder(binding)
            }

        }

    }

    override fun getItemViewType(position: Int): Int {

        return when(formList[position].inputMethod){

            "TextField" -> 0
            "Options" -> 1
            "InputWithOption" -> 2
            else -> 0

        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val item = formList.get(position)
        when (holder) {
            is InputTextViewHolder -> holder.bind(item)
            is InputTextWithOptionsViewHolder -> holder.bind(item)
            is OptionsViewHolder -> holder.bind(item)
        }

    }

    override fun getItemCount(): Int = formList.size

    class InputTextViewHolder(val binding : TextFieldBinding ): RecyclerView.ViewHolder(binding.root){

        fun bind(data: ProductFormInfoListItem){
            binding.inputText.hint =  "Enter ${data.label}"
            binding.textInputLayout.hint = data.label
            when(data.inputType){
                "Int" -> InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            }
            binding.inputText.inputType  = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
        }
    }


    class InputTextWithOptionsViewHolder(val binding : TextWithOptionsFieldBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ProductFormInfoListItem){
            val optionList = ArrayAdapter(binding.root.context, R.layout.show_list, data.options?: arrayOf("none") )
            binding.textInputLayout.hint= "Enter ${data.label}"
            binding.options.hint = "e.g ${data.options?.get(0)}"
            binding.options.setAdapter(optionList)

        }

    }

    class OptionsViewHolder(val binding : OptionsFieldBinding ): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ProductFormInfoListItem){

            val optionList = ArrayAdapter(binding.root.context, R.layout.show_list, data.options!!)
            binding.optionsTv.setAdapter(optionList)
            binding.textInputLayout.hint = data.label
            binding.optionsTv.hint = "e.g ${data.options[0]}"

        }
    }
}