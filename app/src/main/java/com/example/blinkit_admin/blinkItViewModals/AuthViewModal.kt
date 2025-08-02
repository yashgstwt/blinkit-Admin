package com.example.blinkit_admin.blinkItViewModals

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.blinkit.Modals.Admin
import com.example.blinkit_admin.utils.Utils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModal: ViewModel() {

    private var _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)

    private var _signInSuccessfully = MutableStateFlow(false)
    val signInSuccessfully = _signInSuccessfully.asStateFlow()

    private var _navigateToHomeScreen = MutableStateFlow(false)
    val navigateToHomeScreen = _navigateToHomeScreen.asStateFlow()



    init{

        Utils.getFirebaseAuthInstance().currentUser?.let {

            _navigateToHomeScreen.value = true
        }
    }


    fun sendOTP(userNumber:String , activity: Activity){
       val  callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationId.value = verificationId
                _otpSent.value = true
                Utils.hideDialog()
            }
        }
        val options = PhoneAuthOptions.newBuilder(Utils.getFirebaseAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        
    }



     fun signInWithPhoneAuthCredential(otp: String, number: String, admin: Admin) {
        val credential = PhoneAuthProvider.getCredential(_verificationId.value!!, otp)
        Utils.getFirebaseAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("AllAdmin").child("Admin")
                        .child(Utils.getUid()).setValue(admin.copy(uid = Utils.getUid()))
                    _signInSuccessfully.value = true

                } else {
                    Log.d("loginRes", "signInWithPhoneAuthCredential: ${task.exception.toString()}")
                }
            }
    }
}