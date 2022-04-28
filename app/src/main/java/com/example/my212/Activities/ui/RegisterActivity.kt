package com.example.my212.Activities.uia


import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.my212.R
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.Firestore.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*


class RegisterActivity : BaseActivity() {
    private lateinit var First_name:EditText
    private lateinit var Last_name:EditText
    private lateinit var Email:EditText
    private lateinit var Password:EditText
    private lateinit var Con_Password:EditText
    private lateinit var T_C:CheckBox
    private lateinit var btnRegister:Button
    private lateinit var tvLogin:TextView
    private lateinit var tool_register:Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        First_name=findViewById(R.id.et_first_name)
        Last_name=findViewById(R.id.et_last_name)
        Email=findViewById(R.id.et_email)
        Password=findViewById(R.id.et_password)
        Con_Password=findViewById(R.id.et_confirm_password)
        T_C=findViewById(R.id.cb_terms_and_condition)
        btnRegister=findViewById(R.id.btn_register)
        tvLogin=findViewById(R.id.tv_login)
        tool_register=findViewById(R.id.toolbar_register_activity)


        setUpActionBar()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        tvLogin.setOnClickListener {
           onBackPressed()
        }
        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun setUpActionBar() {

        setSupportActionBar(tool_register)

        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }
        tool_register.setNavigationOnClickListener {
            onBackPressed()
        }
    }



       private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(First_name.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                false
            }
            TextUtils.isEmpty(Last_name.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name),true)
                false
            }
            TextUtils.isEmpty(Email.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(Password.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password),true)
                false
            }
            TextUtils.isEmpty(Con_Password.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password),true)
                false
            }
           Password.text.toString().trim { it<= ' ' } != Con_Password.text.toString().trim { it<= ' '} ->{
               showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),true)
               false
            }
            !T_C.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_to_terms_and_condition),true)
                false
            }
            else -> {
                showErrorSnackBar(resources.getString(R.string.registry_successful),false)
                true
            }

        }
    }


    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = Email.text.toString().trim { it <= ' ' }
            val password: String = Password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> {task ->

                        if (task.isSuccessful){
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            val user=User(
                                firebaseUser.uid,
                                First_name.text.toString().trim { it <= ' ' },
                               Last_name.text.toString().trim { it <= ' ' },
                                Email.text.toString().trim { it <= ' ' }
                            )
                            FirestoreClass().registerUser(this@RegisterActivity,user)

                            //FirebaseAuth.getInstance().signOut()
                           // finish()


                        }else{
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
                )
        }
    }
    fun userRegistrationSuccess(){
        hideProgressDialog()

        Toast.makeText(this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_LONG).show()
    }
}





