package com.example.my212.Activities.uia

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.my212.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var Tool_forgot_pass: Toolbar
    lateinit var btnSubmit: Button
    private lateinit var Email_forgot: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        Tool_forgot_pass=findViewById(R.id.toolbar_forgot_password_activity)
        btnSubmit=findViewById(R.id.submit)
        Email_forgot=findViewById(R.id.et_email_forgot)


        setUpActionBar()

    }
    private fun setUpActionBar() {

        setSupportActionBar(Tool_forgot_pass)

        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }
        Tool_forgot_pass.setNavigationOnClickListener {
            onBackPressed()


        btnSubmit.setOnClickListener {
            val email: String = Email_forgot.text.toString().trim { it <= ' ' }
            if (email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener {task->
                        hideProgressDialog()
                        if (task.isSuccessful){
                            Toast.makeText(this@ForgotPasswordActivity,
                                resources.getString(R.string.email_sent_success),
                                Toast.LENGTH_LONG).show()
                            finish()
                        }else{
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }

                    }
            }


        }
    }
}
}