package com.example.my212.Activities.uia

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar

import com.example.my212.R
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.utls.Constants
import com.example.my212.utls.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.example.my212.Firestore.models.User
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(),View.OnClickListener {
   lateinit var toolsettings : Toolbar
   private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolsettings=findViewById(R.id.toolbar_settings_activity)
        setupActionBar()

        tv_edit.setOnClickListener(this)
        btn_logout.setOnClickListener(this)

    }

    private fun setupActionBar(){
      setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        toolsettings.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }
    fun userDetailsSuccess(user:User){

        mUserDetails = user

        hideProgressDialog()

        GlideLoader(this).loadUserPicture(user.image , iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text= user.gender
        tv_email.text= user.email
        tv_mobile_number.text="${user.mobile}"

    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v !=null){
            when(v.id){
                R.id.tv_edit->{
                    val intent = Intent(this@SettingsActivity,UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS,mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

            }
        }
    }
}