package com.example.blinkit_admin.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.example.blinkit_admin.databinding.ProgressDialogBinding
import com.google.firebase.auth.FirebaseAuth


object Utils {

    private var dialog : AlertDialog? = null
    fun showDialog( context: Context , msg : String ){

        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.loginState.text = msg

        dialog = AlertDialog
            .Builder(context)
            .setView(progress.root)
            .setCancelable(false)
            .create()

        dialog?.show()
    }
    fun hideDialog(){
        dialog?.dismiss()
        dialog = null

    }

    fun showToast(context: Context,msg:String ){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()

    }

    private var FirebaseAuthInstance : FirebaseAuth? = null

    fun getFirebaseAuthInstance() : FirebaseAuth {

        if(FirebaseAuthInstance == null){
            FirebaseAuthInstance  = FirebaseAuth.getInstance()
        }

        return FirebaseAuthInstance!!
    }

    fun getUid() : String{
        return FirebaseAuth.getInstance().currentUser?.uid ?: "null"
    }
}