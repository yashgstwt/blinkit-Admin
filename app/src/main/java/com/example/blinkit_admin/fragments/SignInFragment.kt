package com.example.blinkit.Fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.blinkit_admin.R
import com.example.blinkit_admin.utils.Utils.showToast
import com.example.blinkit_admin.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater,container,false)

        getUserNumber()
        onContinueButtonClicked()


        return binding.root
    }

    private fun onContinueButtonClicked() {
        binding.continueBtn.setOnClickListener {
            val mobileNumber = binding.mobileNumberText.text.toString()
            if (mobileNumber.length != 10 || mobileNumber.isEmpty()) {
                showToast(requireContext(), "Enter valid number")
            } else {
                val bundle = Bundle()
                bundle.putString("mobileNumber", mobileNumber)
                findNavController().navigate(R.id.action_signInFragment_to_OTPFragment, bundle)
            }

        }

    }


    private fun getUserNumber(){
        binding.mobileNumberText.addTextChangedListener ( object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(number: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val numberLen = number?.length

                if (numberLen == 10){
                    binding.continueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.secondary))
                } else {
                    binding.continueBtn.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.primary))
                }

            }

            override fun afterTextChanged(p0: Editable?) {}

            }
        )
    }
}