package com.example.blinkit.Fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.blinkit.Modals.Admin
import com.example.blinkit_admin.activity.MainActivity
import com.example.blinkit_admin.R
import com.example.blinkit_admin.utils.Utils
import com.example.blinkit_admin.blinkItViewModals.AuthViewModal
import com.example.blinkit_admin.databinding.FragmentOTPBinding

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private val viewModal: AuthViewModal by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentOTPBinding.inflate(inflater, container, false)

        val otpText = arrayOf(binding.OTP1,binding.OTP2,binding.OTP3,binding.OTP4,binding.OTP5,binding.OTP6)

        binding.ToolBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_OTPFragment_to_signInFragment)
        }

        val mobileNumber = arguments?.getString("mobileNumber").toString()
        binding.mobileNumberText.text = mobileNumber


        binding.LoginBtn.setOnClickListener{
            Utils.showToast(requireContext(),"Signing You... ")
            val otp = otpText.joinToString("") { it.text.toString() }
            if (otp.length < otpText.size){
                Utils.showToast(requireContext(),"Please Enter Valid OTP")
            }
            else{
                otpText.forEach {
                    it.text?.clear()
                    it.clearFocus()
                }
                verifyOTP(otp, mobileNumber)
            }
        }

        sendOTP(mobileNumber , requireActivity())

        otpText.forEachIndexed { index , element->
            otpText[index].addTextChangedListener(
                object :TextWatcher{
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p: Editable?) {
                      if(p?.length == 1 && index < otpText.size -1){
                                otpText[index+1].requestFocus()
                      }
                      else if(p?.length == 0 && index > 0) {
                          otpText[index-1].requestFocus()

                      }
                    }
                }
            )
        }


        return binding.root
    }

    private fun verifyOTP(otp: String , mobileNumber: String ) {
        Utils.showDialog(requireContext(),"Verifying OTP")

        val admin = Admin(uid = Utils.getUid() , phoneNumber = mobileNumber)
        viewModal.signInWithPhoneAuthCredential(otp, mobileNumber , admin)

        lifecycleScope.launch {
            viewModal.signInSuccessfully.collectLatest{
                if (it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext() , "Login Successfully")
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()

                }
            }

        }
    }


    private fun sendOTP(userNumber: String , activity: Activity){
        Utils.showDialog(requireContext(),"Sending.. OTP")
        viewModal.apply {
            viewModal.sendOTP(userNumber, activity)

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    otpSent.collectLatest { otpSentValue ->
                        if (otpSentValue) {
                            Utils.hideDialog()
                            Utils.showToast(requireContext(), "OTP sent")
                        }
                    }
                }
            }
        }
    }
}