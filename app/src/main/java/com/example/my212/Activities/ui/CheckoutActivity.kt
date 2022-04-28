package com.example.my212.Activities.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my212.Activities.Adapter.CartItemsListAdapter
import com.example.my212.Activities.uia.BaseActivity
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.Firestore.models.Address
import com.example.my212.Firestore.models.CartItem
import com.example.my212.Firestore.models.Order
import com.example.my212.Firestore.models.Product
import com.example.my212.R
import com.example.my212.utls.Constants
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {
//    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSelectedAddressDetails: Address? = null
    private lateinit var toolchekout:Toolbar
    private var mSubTotal : Double  = 0.0
    private var mTotalAmount : Double  = 0.0
    private lateinit var mOrderDetails : Order

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_checkout)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mSelectedAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)
        }
        if (mSelectedAddressDetails != null) {
            tv_checkout_address.text =
                "${mSelectedAddressDetails!!.address}, ${mSelectedAddressDetails!!.zipcode}"
           tv_checkout_additional_note.text = mSelectedAddressDetails!!.additionalNote
            tv_checkout_address_type.text = mSelectedAddressDetails!!.type
            tv_checkout_full_name.text = mSelectedAddressDetails!!.name
            if (mSelectedAddressDetails!!.otherDetails.isNotEmpty()) {
                tv_checkout_other_details.text = mSelectedAddressDetails!!.otherDetails
            } else {
                tv_checkout_other_details.visibility = View.GONE
            }
            tv_mobile_number.text = mSelectedAddressDetails!!.mobileNumber
        }
        getProductList()

        btn_place_order.setOnClickListener{
            placeAnOrder()
        }
    }

    private fun getProductList() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this@CheckoutActivity)
    }

    fun successProductListFromFirestore(productList: ArrayList<Product>) {
        mProductList = productList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CheckoutActivity)
    }
    private fun placeAnOrder(){
        showProgressDialog(getString(R.string.please_wait))

        if (mSelectedAddressDetails != null){
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemsList,
                mSelectedAddressDetails!!,
                "My Order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "150.00",
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            FirestoreClass().placeOrder(this@CheckoutActivity,mOrderDetails)
        }
    }

    @SuppressLint("SetTextI18n")
    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                }

            }
        }
        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for(item in mCartItemsList){
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0){
                val quantity = item.cart_quantity.toInt()
                val price = item.price.toInt()
                //calculating subtotal

                mSubTotal += (price * quantity)
            }
        }
        tv_checkout_sub_total.text = "Ksh.${mSubTotal}"
        //we can use our own logic here in shipping charges
        tv_checkout_shipping_charge.text = "Ksh.150.0"

        if (mSubTotal > 0){
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 150.0

            tv_checkout_total_amount.text = "Ksh.${mTotalAmount}"
        }else{
            ll_checkout_place_order.visibility = View.GONE
        }


    }

    private fun setUpActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        toolchekout.setNavigationOnClickListener { onBackPressed() }

    }
    fun orderPlaceSuccess(){
        FirestoreClass().updateAllDetails(this,mCartItemsList,mOrderDetails)

    }
    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this, "Your order was placed successfully.", Toast.LENGTH_SHORT).show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        //to clear the stack of activities or layer of activities and open the dashboard activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

    }
}