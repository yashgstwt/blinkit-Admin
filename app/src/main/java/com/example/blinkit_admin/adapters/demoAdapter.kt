package com.example.blinkit_admin.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkit_admin.databinding.CardBinding
import com.example.blinkit_admin.databinding.TextFieldBinding
import com.example.blinkit_admin.modals.dataClasses.Item

class DemoAdapter(val list: List<Item>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {

        val binding = CardBinding.inflate(LayoutInflater.from(parent.context), parent , false)
       return DemoViewHolder(binding);

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        val data  = list[position]

        when(holder){
            is DemoViewHolder -> holder.bind(data)
        }


    }

    override fun getItemCount(): Int = list.size


    class DemoViewHolder(val binding : CardBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind( data: Item){
            binding.textView.text = data.text
            binding.imageview.id = data.image
        }
    }
}