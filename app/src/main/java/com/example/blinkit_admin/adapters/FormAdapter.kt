package com.example.blinkit_admin.adapters

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
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
import com.example.blinkit_admin.modals.dataClasses.FormInputData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Arrays
import kotlin.math.log

class FormAdapter( val viewModal: AddProductViewModal) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var formList : List<ProductFormInfoListItem> = emptyList()


    init {
        CoroutineScope(Dispatchers.IO).launch {
            viewModal.productTypeForm?.collect {
                formList = it
                withContext(Dispatchers.Main) {
                    notifyDataSetChanged()
                }
//                Log.d("yash" , "from init block of formAdapter : ${viewModal.formData}")
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
        val item = formList[position]
        when (holder) {
            is InputTextViewHolder -> holder.bind(item, viewModal , position)
            is InputTextWithOptionsViewHolder -> holder.bind(item , viewModal , position)
            is OptionsViewHolder -> holder.bind(item , viewModal, position)
        }

    }

    override fun getItemCount(): Int = formList.size

    class InputTextViewHolder(val binding : TextFieldBinding ): RecyclerView.ViewHolder(binding.root){

        fun bind(data: ProductFormInfoListItem, viewModal: AddProductViewModal, position: Int){
            binding.inputText.hint =  "Enter ${data.label}"
            binding.textInputLayout.hint = data.label

            when(data.inputType){
                "Int" -> InputType.TYPE_TEXT_FLAG_AUTO_CORRECT
            }

            if(viewModal.formData != null && position < viewModal.formData.size){
                binding.inputText.setText(viewModal.formData[position]?.value)
            }

            binding.inputText.inputType  = InputType.TYPE_TEXT_FLAG_AUTO_CORRECT

            binding.inputText.removeTextChangedListener(binding.inputText.getTag(R.id.text_watcher_tag) as? TextWatcher)

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (position < viewModal.formData.size) {
                        viewModal.formData[position] = FormInputData(label = data.label.toString(), value = s.toString())
                        Log.d("yash", viewModal.formData.contentToString())
                    }
                }
            }

            binding.inputText.addTextChangedListener(textWatcher)
            binding.inputText.setTag(R.id.text_watcher_tag, textWatcher)
        }
    }


    class InputTextWithOptionsViewHolder(val binding : TextWithOptionsFieldBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ProductFormInfoListItem, viewModal: AddProductViewModal, position: Int){
            val optionList = ArrayAdapter(binding.root.context, R.layout.show_list, data.options?: arrayOf("none") )
            binding.textInputLayout.hint= "Enter ${data.label}"
            binding.options.hint = "e.g ${data.options?.getOrNull(0) ?: ""}"
            binding.options.setAdapter(optionList)

            var inputText = ""
            var selectedText = ""

            if (position < viewModal.formData.size && viewModal.formData[position]?.value != null) {
                val inputData = viewModal.formData[position]?.value?.split(' ')
                if (inputData != null) {
                    inputText = inputData.getOrNull(0).toString()
                    selectedText = inputData.getOrNull(1).toString()
                    binding.input.setText(inputText)
                    binding.options.setText(selectedText, false) // Set false to prevent filtering
                }
            }

            binding.options.setOnItemClickListener { _, _, itemPosition, _ ->
               selectedText = data.options?.getOrNull(itemPosition).toString()

                if (position < viewModal.formData.size) {
                    viewModal.formData[position] = FormInputData(label = data.label.toString(), value = "$inputText $selectedText")
                    Log.d("yash", Arrays.toString(viewModal.formData))
                }
            }

            binding.input.removeTextChangedListener(binding.input.getTag(R.id.text_watcher_tag) as? TextWatcher)
            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    inputText = s.toString()
                    if (position < viewModal.formData.size) {
                        viewModal.formData[position] = FormInputData(label = data.label.toString(), value = "$inputText $selectedText")
                        Log.d("yash", Arrays.toString(viewModal.formData))
                    }
                }
            }
            binding.input.addTextChangedListener(textWatcher)
            binding.input.setTag(R.id.text_watcher_tag, textWatcher)


        }

    }

    class OptionsViewHolder(val binding : OptionsFieldBinding ): RecyclerView.ViewHolder(binding.root){
        fun bind(data: ProductFormInfoListItem, viewModal: AddProductViewModal, holderPosition: Int){

            val optionList = ArrayAdapter(binding.root.context, R.layout.show_list, data.options ?: emptyArray<String>())
            binding.optionsTv.setAdapter(optionList)
            binding.textInputLayout.hint = data.label
            binding.optionsTv.hint = "e.g ${data.options?.getOrNull(0) ?: ""}"


            if (holderPosition < viewModal.formData.size && viewModal.formData[holderPosition]?.value != null){
                binding.optionsTv.setText(viewModal.formData[holderPosition]?.value, false)
            }

            binding.optionsTv.setOnItemClickListener { _, _, itemPosition, _ ->

                val ans = data.options?.getOrNull(itemPosition)
                if (ans != null && holderPosition < viewModal.formData.size) {
                    viewModal.formData[holderPosition] = FormInputData(label = data.label.toString(), value = ans)
                    Log.d("yash", viewModal.formData.contentToString())
                }
            }
        }
    }
}