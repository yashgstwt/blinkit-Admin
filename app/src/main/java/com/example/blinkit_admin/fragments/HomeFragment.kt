package com.example.blinkit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.blinkit_admin.R
import com.example.blinkit_admin.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentHomeBinding.inflate(inflater,container,false)

        setStatusBarColor()



        return binding.root
    }

    fun setStatusBarColor(){
        activity?.window?.apply{
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.tertiary)
            statusBarColor = statusBarColors
        }
    }

}