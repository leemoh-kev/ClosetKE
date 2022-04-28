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
import com.example.my212.Firestore.models.CartItem
import com.example.my212.Firestore.models.Product
import com.example.my212.R
import com.example.my212.utls.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {
//    private lateinit var binding : ActivityCartListBinding
    private lateinit var toolcart:Toolbar
    private lateinit var mProductList : ArrayList<Product>
    private lateinit var mCartListItem : ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_cart_list)
        toolcart=findViewById(R.id.toolbar_cart_list_activity)

        setUpActionBar()

        btn_checkout.setOnClickListener{
            val intent = Intent(this@CartListActivity,AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
            startActivity(intent)
        }
    }
    private fun setUpActionBar(){

        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        toolcart.setNavigationOnClickListener { onBackPressed() }

    }

    override fun onResume() {
        super.onResume()
//        getCartItemList() // when getting product list success then we call get cart items list
        //we need to get the product list before cart items so that we know the quantity that user can buy
        getProductList()
    }

    private fun getCartItemList(){
//        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getCartList(this)
    }
    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemList()
    }
    @SuppressLint("SetTextI18n")
    fun successCartItemList(cartList : ArrayList<CartItem>){
        hideProgressDialog()

        for(product in mProductList){
            for (cartItem in cartList){
                //make sure that the product in the cart
                if (product.product_id == cartItem.product_id){
                    //we just assign the product quantity to the cart item quantity
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItem = cartList

        if (mCartListItem.size >0){
           rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this)
            rv_cart_items_list.setHasFixedSize(true)
            val adapter = CartItemsListAdapter(this, mCartListItem,true)
            rv_cart_items_list.adapter = adapter

            var subTotal  = 0.0
            var price = 0
            for(item in mCartListItem){
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {

                    price = when {
                        item.price.contains(",") -> {
                            val index = item.price.indexOf(",")
                            val s1 = item.price.substring(0,index)
                            val s2 = item.price.substring(index+1,item.price.length)

                            (s1 + s2).toInt()
                        }
                        item.price.contains(".") -> {
                            val index = item.price.indexOf(".")
                            val s1 = item.price.substring(0,index)
                            val s2 = item.price.substring(index+1,item.price.length)

                            (s1 + s2).toInt()
                        }
                        else -> {
                            item.price.toInt()
                        }
                    }
                    val quantity = item.cart_quantity.toDouble()
                    subTotal += (price * quantity)
                }
            }
          tv_sub_total.text = "Ksh.${subTotal}"
            //Change the logic accordingly
           tv_shipping_charge.text = "" +
                   "Ksh.${150}"

            if (subTotal >0 ){
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 150

                tv_total_amount.text = "Ksh.${total}"
            }else{
                ll_checkout.visibility = View.GONE
            }
        }else{
            tv_no_cart_item_found.visibility = View.VISIBLE
            ll_checkout.visibility = View.GONE
         rv_cart_items_list.visibility = View.GONE
        }
    }

    fun successProductListFromFireStore(productList : ArrayList<Product>){

        mProductList = productList
        hideProgressDialog()
        //after getting product list
        getCartItemList()

    }
    private fun getProductList(){
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getAllProductList(this)
    }
    fun itemRemoveSuccess(){
        hideProgressDialog()
        Toast.makeText(this, getString(R.string.msg_item_removed_successfully), Toast.LENGTH_SHORT).show()

        getCartItemList()
    }

    override fun onDestroy() {
        hideProgressDialog()
        super.onDestroy()
    }
}