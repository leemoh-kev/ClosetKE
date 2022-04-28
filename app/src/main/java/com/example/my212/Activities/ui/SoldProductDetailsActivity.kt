package com.example.my212.Activities.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.my212.Firestore.models.SoldProduct
import com.example.my212.R
import com.example.my212.utls.Constants
import com.example.my212.utls.GlideLoader
import kotlinx.android.synthetic.main.activity_sold_product_details.*
import java.text.SimpleDateFormat
import java.util.*

class SoldProductDetailsActivity : AppCompatActivity() {
//    private lateinit var binding : ActivitySoldProductDetailsBinding
    private lateinit var toolsold:Toolbar
    private  var mSoldProductDetails : SoldProduct? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivitySoldProductDetailsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sold_product_details)
        toolsold=findViewById(R.id.toolbar_sold_product_details_activity)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)){
            mSoldProductDetails = intent.getParcelableExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)
            setUpUI(mSoldProductDetails!!)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_sold_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        toolsold.setNavigationOnClickListener { onBackPressed() }
    }
    @SuppressLint("SetTextI18n")
    private fun setUpUI(productDetails : SoldProduct){

        tv_order_details_id.text = productDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        val orderDateTime = formatter.format(calendar.time)

        tv_order_details_date.text = orderDateTime

        GlideLoader(this).loadProductPicture(productDetails.image,iv_product_item_image)
        tv_product_item_name.text = productDetails.title
        tv_product_item_price.text = "Ksh.${productDetails.price}"
        tv_sold_product_quantity.text = productDetails.sold_quantity


        tv_my_order_details_address_type.text = productDetails.address.type
        tv_my_order_details_full_name.text = productDetails.address.name
        tv_my_order_details_address.text = "${productDetails.address.address} , ${productDetails.address.zipcode}"
        tv_my_order_details_additional_note.text = productDetails.address.additionalNote

        if (productDetails.address.otherDetails.isNotEmpty()){
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = productDetails.address.otherDetails
        }else{
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = productDetails.address.mobileNumber

        tv_order_details_sub_total.text = productDetails.sub_total_amount
        tv_order_details_shipping_charge.text = productDetails.shipping_charge
        tv_order_details_total_amount.text = productDetails.total_amount
    }
}