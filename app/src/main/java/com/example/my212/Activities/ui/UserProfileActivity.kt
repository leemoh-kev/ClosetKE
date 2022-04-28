package com.example.my212.Activities.uia



import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.my212.R
import com.example.my212.Firestore.models.User
import com.example.my212.utls.Constants
import kotlinx.android.synthetic.main.activity_user_profile.*
import android.content.pm.PackageManager

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.my212.Activities.ui.DashboardActivity
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.utls.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.iv_user_photo
import java.io.IOException

class UserProfileActivity : BaseActivity(),View.OnClickListener {
    private lateinit var mUserDetails: User
    private lateinit var toolUser: Toolbar
    private var mSelectedImageFileUri: Uri?=null
    private var mUserProfileImageURL:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        toolUser=findViewById(R.id.toolbar_user_profile)




        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)){
            mUserDetails=intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        eT_first_name.setText(mUserDetails.firstName)
        eT_last_name.setText(mUserDetails.lastName)
        eT_email.isEnabled=false
        eT_email.setText(mUserDetails.email)



        if (mUserDetails.profileCompleted==0){
            tv_title.text=resources.getString(R.string.title_complete_profile)
            eT_first_name.isEnabled=false

            eT_last_name.isEnabled=false


        }else{
            setupActionBar()
            tv_title.text=resources.getString(R.string.title_edit_profile)

            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails .image , iv_user_photo)



            if (mUserDetails.mobile !=0L){
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }

            if (mUserDetails.gender == Constants.MALE){
                rb_male.isChecked=true
            }else{
                rb_female.isChecked=true
            }

        }



        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener (this@UserProfileActivity)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_user_profile)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        toolUser.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null){

            when (v.id){

                R.id.iv_user_photo-> {
                    if (ContextCompat.checkSelfPermission(
                            this,Manifest.permission.READ_EXTERNAL_STORAGE
                        )==PackageManager.PERMISSION_GRANTED
                    ){
//                        showErrorSnackBar("You already have the storage permission.",false)
                        Constants.showImageChooser(this)
                    }else{
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit->{
                    if (validateUserProfileDetails()){
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if (mSelectedImageFileUri !=null){
                            FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri)
                        } else{
                            updateUserProfileDetails()
                        }
                        //showErrorSnackBar("Your details are valid",false)
                    }
                }
            }
        }
    }
    private fun updateUserProfileDetails(){
        val userHashMap=HashMap<String,Any>()

        val mobileNumber=et_mobile_number.text.toString().trim { it <= ' ' }


        val firstName=eT_first_name.text.toString().trim { it <= ' ' }
        if (firstName!=mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME]=firstName
        }
        val lastName=eT_last_name.text.toString().trim { it <= ' ' }
        if (lastName!=mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME]=lastName
        }


        val gender=if (rb_male.isChecked){
            Constants.MALE
        }else{
            Constants.FEMALE
        }
        if (mUserProfileImageURL.isEmpty()){
            userHashMap[Constants.IMAGE]=mUserProfileImageURL
        }
        if (mobileNumber.isEmpty() && mobileNumber != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE]=mobileNumber.toLong()
        }
        if (gender.isEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER]=gender
        }
        userHashMap[Constants.GENDER]=gender
        userHashMap[Constants.COMPLETE_PROFILE]=1

       showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this,userHashMap)

    }



    fun userProfileUpdateSuccess(){
        hideProgressDialog()
        Toast.makeText(this@UserProfileActivity,resources.getString(R.string.msg_profile_update_success),Toast.LENGTH_SHORT
        )
            .show()
        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
               // showErrorSnackBar("The storage permission is granted.",false)
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,resources.getString(R.string.read_storage_permission_denied),Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode==Activity.RESULT_OK){
            if (requestCode==Constants.PICK_IMAGE_REQUEST_CODE){
                if (data!=null){
                    try {
                        mSelectedImageFileUri=data.data!!

                        //iv_user_photo.setImageURI(selectedImageFileUri)

                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_user_photo)


                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@UserProfileActivity,resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }
        }else if(resultCode==Activity.RESULT_CANCELED){
            Log.e("Request cancelled","Image selection cancelled")
        }
    }
    private fun validateUserProfileDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' })->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            }
            else->{
                true
            }
        }
    }
    fun imageUploadSuccess(imageURL: String){
        hideProgressDialog()
        Toast.makeText(this@UserProfileActivity,"your image is uploaded successfully.Image URL is $imageURL",
        Toast.LENGTH_SHORT).show()
        mUserProfileImageURL=imageURL
        updateUserProfileDetails()
    }

}
