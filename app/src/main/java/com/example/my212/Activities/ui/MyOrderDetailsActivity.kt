package com.example.my212.Activities.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my212.Activities.Adapter.CartItemsListAdapter
import com.example.my212.Firestore.models.Order
import com.example.my212.R
import com.example.my212.utls.Constants
import kotlinx.android.synthetic.main.activity_my_order_details.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
//    private lateinit var binding : ActivityMyOrderDetailsBinding
    private lateinit var toolmyorder:Toolbar
    private var mMyOrderDetails : Order? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_my_order_details)
        toolmyorder=findViewById(R.id.toolbar_my_order_details_activity)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)){
            mMyOrderDetails = intent.getParcelableExtra(Constants.EXTRA_MY_ORDER_DETAILS)
            setUpUI(mMyOrderDetails!!)
        }
    }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_my_order_details_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        toolmyorder.setNavigationOnClickListener { onBackPressed() }
    }
    @SuppressLint("SetTextI18n")
    private fun setUpUI(orderDetails : Order){

        tv_order_details_id.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_dateTime
        val orderDateTime = formatter.format(calendar.time)

        tv_order_details_date.text = orderDateTime

        //for status processing placed and delivered,
        //we find the time difference btw in booking time and passed time
        val diffInMilliSeconds : Long = System.currentTimeMillis() - orderDetails.order_dateTime
        //we convert the milli to hours
        val diffInHours : Long = TimeUnit.MICROSECONDS.toHours(diffInMilliSeconds)

        when{
            //if time is less than 1 hour
            diffInHours < 1->{
                tv_order_status.text = getString(R.string.order_status_pending)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent)
                )

            }
            //if time is less than 2 hour
            diffInHours < 2->{
               tv_order_status.text = getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess)
                )

            }
            //if time is greater than 2 hour
            else->{
                tv_order_status.text = getString(R.string.order_status_delivered)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered)
                )

            }
        }

        rv_my_order_items_list.layoutManager = LinearLayoutManager(this)
        rv_my_order_items_list.setHasFixedSize(true)

        val adapter = CartItemsListAdapter(this,orderDetails.items,false)
        rv_my_order_items_list.adapter = adapter


        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text = "${orderDetails.address.address} , ${orderDetails.address.zipcode}"
        tv_my_order_details_additional_note.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()){
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails
        }else{
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber

        tv_order_details_sub_total.text = orderDetails.sub_total_amount
        tv_order_details_shipping_charge.text = orderDetails.shipping_charge
        tv_order_details_total_amount.text = orderDetails.total_amount
    }
}