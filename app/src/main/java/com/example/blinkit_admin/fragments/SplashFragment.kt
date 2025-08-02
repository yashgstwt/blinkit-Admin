package com.example.blinkit.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.blinkit_admin.activity.MainActivity
import com.example.blinkit_admin.R
import com.example.blinkit_admin.blinkItViewModals.AuthViewModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {
    private val viewModal: AuthViewModal by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)
            viewModal.navigateToHomeScreen.collect{
                if (it){
                    startActivity(Intent(requireContext() , MainActivity::class.java))

                }else {
                    findNavController().navigate(R.id.action_splashFragment_to_signInFragment)
                }
            } 
        }
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }
}