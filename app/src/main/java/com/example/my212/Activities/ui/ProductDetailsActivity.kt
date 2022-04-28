package com.example.my212.Activities.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.my212.Activities.uia.BaseActivity
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.Firestore.models.CartItem
import com.example.my212.Firestore.models.Product
import com.example.my212.R
import com.example.my212.utls.Constants
import com.example.my212.utls.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity() , View.OnClickListener {

    private var mProductID : String = ""
    private var mOwnerID : String = ""
    private lateinit var mProductDetails : Product
    private lateinit var  toolproduct:Toolbar
//    private lateinit var binding : ActivityProductDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_product_details)

        toolproduct=findViewById(R.id.toolbar_product_details_activity)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID) && intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)){
            mProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            mOwnerID = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            Log.i("Product id ::",mProductID)
        }

        if (mOwnerID == FirestoreClass().getCurrentUserID()){
            btn_add_to_cart.visibility = View.GONE
            btn_add_to_cart.visibility = View.GONE
        }else{
            btn_add_to_cart.visibility = View.VISIBLE
        }

        setUpActionBar()

        getProductDetails()

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        toolproduct.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getProductDetails(){

        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this,mProductID)
    }
    @SuppressLint("SetTextI18n")
    fun productDetailsSuccess(product : Product){

        mProductDetails = product

//        hideProgressDialog()

        GlideLoader(this).loadProductPicture(product.image,iv_product_detail_image)
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "Ksh.${product.price}"
        tv_product_details_available_quantity.text = product.stock_quantity
        tv_product_details_description.text = product.description

        if (product.stock_quantity.toInt() == 0){
            hideProgressDialog()
            btn_add_to_cart.visibility = View.GONE
            tv_product_details_available_quantity.text = getString(R.string.lbl_text_out_of_stock)
            tv_product_details_available_quantity.setTextColor(
                ContextCompat.getColor(this,R.color.colorSnackBarError)
            )
        }else{
            //check if his own product or not , No need to check everytime
            //we check only for those products that not created by user
            if (FirestoreClass().getCurrentUserID() == product.user_id){
                hideProgressDialog()
            }else{
                FirestoreClass().checkIfItemsExistsInCart(this,mProductID)
            }
        }


    }

    private fun addToCart(){
        val addToCart = CartItem(
            FirestoreClass().getCurrentUserID(),
            mOwnerID,
            mProductID,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CARD_QUANTITY,
            mProductDetails.stock_quantity,
        )

        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().addCartItems(this,addToCart)

    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.btn_add_to_cart->{
                    addToCart()

                }
                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity,CartListActivity::class.java))
                }
            }
        }
    }
    fun productExistsInCart(){
        hideProgressDialog()

        btn_go_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this@ProductDetailsActivity, getString(R.string.success_message_item_added_in_cart), Toast.LENGTH_SHORT).show()


        btn_go_to_cart.visibility = View.VISIBLE
        btn_add_to_cart.visibility = View.GONE
    }
}