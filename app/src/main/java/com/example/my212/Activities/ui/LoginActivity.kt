package com.example.my212.Activities.uia

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import com.example.my212.Activities.ui.DashboardActivity
import com.example.my212.R
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.Firestore.models.User
import com.example.my212.utls.Constants
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity(),View.OnClickListener {
    lateinit var headerimage:FrameLayout
    lateinit var tvtitle:TextView
    lateinit var tillemail:TextInputLayout
    lateinit var etemail:EditText
    lateinit var tillpassword:TextInputLayout
    lateinit var etpassword:EditText
    lateinit var forgotpass:TextView
    lateinit var btnLogin:Button
    lateinit var donthaveaccount:TextView
    lateinit var regster:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        headerimage=findViewById(R.id.fl_header_image)
        tvtitle=findViewById(R.id.tv_title)
        tillemail=findViewById(R.id.til_email)
        etemail=findViewById(R.id.et_email)
        tillpassword=findViewById(R.id.til_password)
        etpassword=findViewById(R.id.et_password)
        forgotpass=findViewById(R.id.tv_forgot_password)
        btnLogin=findViewById(R.id.btn_login)
        donthaveaccount=findViewById(R.id.tv_don_t_have_account)
        regster=findViewById(R.id.tv_register)






        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        forgotpass!!.setOnClickListener(this)
        btnLogin!!.setOnClickListener(this)
        regster!!.setOnClickListener(this)


//        regster!!.setOnClickListener {
//            val intent=Intent(this@LoginActivity,RegisterActivity::class.java)
//            startActivity(intent)
//
//        }
    }

    fun userLoggedInSuccess(user:User){
        hideProgressDialog()

        Log.i("First Name:",user.firstName)
        Log.i("Last Name:",user.lastName)
        Log.i("Email:",user.email)


        if (user.profileCompleted==0){
            val intent=Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)

            startActivity(intent)
        }else{
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
        }
        finish()
    }


    override fun onClick(view:View?) {
        if (view!=null){
            when (view.id){
                R.id.tv_forgot_password->{
                    val intent=Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                    finish()

                }
                R.id.btn_login->{
                    logInRegisteredUsers()
                }
                R.id.tv_register->{
                    val intent=Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
    private fun validateLoginDetails():Boolean{
        return when {
            TextUtils.isEmpty(etemail.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(etpassword.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            else->{
                true
            }
        }
    }
    private fun logInRegisteredUsers(){
        if (validateLoginDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = etemail.text.toString().trim { it <= ' ' }
            val password: String = etpassword.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                        if (task.isSuccessful){
                            FirestoreClass().getUserDetails(this@LoginActivity)

                        }else{
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }



        }
    }


}