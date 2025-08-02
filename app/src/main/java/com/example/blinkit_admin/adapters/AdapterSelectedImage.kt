package com.example.blinkit_admin.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.blinkit_admin.databinding.RcvAddProductImgItemBinding

class AdapterSelectedImage( val imageUri : ArrayList<Uri>): RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder>() {

    class SelectedImageViewHolder (val binding : RcvAddProductImgItemBinding) : ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {

        return SelectedImageViewHolder(RcvAddProductImgItemBinding.inflate(LayoutInflater.from(parent.context) , parent,false))
    }

    override fun getItemCount(): Int {
        return imageUri.size
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {

        holder.binding.apply {
            ivImage.setImageURI(imageUri[position])
            closeBtn.setOnClickListener{
                if (position < imageUri.size){
                    imageUri.removeAt(position)
                    notifyItemRemoved(position)
                }

            }
        }

    }
}