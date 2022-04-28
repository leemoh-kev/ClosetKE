package com.example.my212.Activities.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.my212.Activities.uia.BaseActivity
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.Firestore.models.Product
import com.example.my212.R
import com.example.my212.utls.Constants
import com.example.my212.utls.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*

class AddProductActivity : BaseActivity(), View.OnClickListener {
    private lateinit var tooladdproduct: Toolbar

    private var mSelectedImageFileUri : Uri? = null
    private var mProductImageURL : String = "imageURL"
//    private var binding : ActivityAddProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//       binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_add_product)
    tooladdproduct=findViewById(R.id.toolbar_add_product_activity)




        setUpActionBar()
       btn_submit.setOnClickListener(this@AddProductActivity)
        iv_add_update_product.setOnClickListener(this@AddProductActivity)
    }

    private fun setUpActionBar(){

        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        tooladdproduct.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.iv_add_update_product->{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this)
                    }else{
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
                R.id.btn_submit->{
                    if (validateProductDetails()){
                        uploadProductImage()
                    }

                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null) {
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit_24))

                    mSelectedImageFileUri = data.data
                    GlideLoader(this).loadUserPicture(data.data!!,iv_product_image)
                }
            }
        }else{
            Log.e("Cancelled ::", "Cancelled by user")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this, getString(R.string.read_storage_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun uploadProductImage(){
        showProgressDialog(getString(R.string.please_wait))

        FirestoreClass().uploadImageToCloudStorage(this,mSelectedImageFileUri)
        Constants.PRODUCT_IMAGE
//        mProductImageURL = imageURL
//        showErrorSnackBar("Image upload successfully $imageURL",false)
        uploadProductDetails()
    }
    private fun validateProductDetails() : Boolean{
        return when{

            mSelectedImageFileUri == null ->{
                showErrorSnackBar(getString(R.string.error_message_product_image),true)
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_title),true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_price),true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_description),true)
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(getString(R.string.error_message_product_quantity),true)
                false
            }
            else ->{
                true

            }
        }
    }
//   fun imageUploadSuccess(imageURL : String){
//
//
//
//
////        Upload product details to the storage
//
//
//
//
//    }
    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.product_upload_success_message), Toast.LENGTH_SHORT).show()

        finish()

    }

    private fun uploadProductDetails(){

        val shared = getSharedPreferences(Constants.MYCLOSET_PREFERENCES, Context.MODE_PRIVATE)
        val userName = shared.getString(Constants.LOGGED_IN_USERNAME,"")


        val productDetails = Product(
            FirestoreClass().getCurrentUserID(),
            userName!!,
            et_product_title.text.toString().trim{ it <= ' '},
            et_product_price.text.toString().trim{ it<= ' '},
            et_product_description.text.toString().trim{it <= ' '},
            et_product_quantity.text.toString().trim{ it <= ' '},
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this,productDetails)

    }

    override fun onDestroy() {
        hideProgressDialog()
        super.onDestroy()
    }
}