package com.example.my212.Activities.uia

import android.app.Dialog
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.my212.R
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {


    private lateinit var mProgressDialog : Dialog
    private var doubleBackToExitPressedOnce = false

    fun showErrorSnackBar(message : String, errorMessage : Boolean) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content),message,Snackbar.LENGTH_LONG)

        val snackBarView = snackBar.view

        if (errorMessage){
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarError)
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarSuccess)
            )
        }
        snackBar.show()
    }


fun showProgressDialog(message: String) {

    mProgressDialog = Dialog(this)

    mProgressDialog.setContentView(R.layout.dialog_progress)
    mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text=message
    mProgressDialog.setCancelable(false)
    mProgressDialog.setCanceledOnTouchOutside(false)
    mProgressDialog.show()
}
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
    fun doubleBackToExit(){

        if (doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true

        Toast.makeText(this, getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT).show()

        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                doubleBackToExitPressedOnce = false
            },
            1500
        )


    }

    }









