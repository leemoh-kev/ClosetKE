package com.example.my212.Activities.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my212.Activities.Adapter.AddressListAdapter
import com.example.my212.Activities.uia.BaseActivity
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.Firestore.models.Address
import com.example.my212.R
import com.example.my212.utls.Constants
import com.example.my212.utls.SwipeToDeleteCallback
import com.example.my212.utls.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressListActivity : BaseActivity() {
//    private lateinit var binding: ActivityAddressListBinding
    private var mSelectAddress: Boolean = false
    private lateinit var  tooladdress:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_address_list)
        tooladdress=findViewById(R.id.toolbar_address_list_activity)

        setUpActionBar()
        @Suppress("DEPRECATION")
        tv_add_address.setOnClickListener {
            val intent = Intent(this, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }
        getAddressList()


    }
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.ADD_ADDRESS_REQUEST_CODE){
            getAddressList()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        tooladdress.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUpAddressInUI(addressList: ArrayList<Address>) {
//        hideProgressDialog()

        shimmer_view_container.visibility = View.GONE
        shimmer_view_container.stopShimmerAnimation()

        if (mSelectAddress) {
            tv_title.text = getString(R.string.select_address)
            if (addressList.size > 0) {
                tv_add_address.visibility = View.GONE
            }else{
                tv_add_address.visibility = View.VISIBLE
            }

        }

        if (addressList.size > 0) {
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this)
            rv_address_list.setHasFixedSize(true)
            val adapter = AddressListAdapter(this, addressList,mSelectAddress)
            rv_address_list.adapter = adapter

            if (!mSelectAddress) {

                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapters = rv_address_list.adapter as AddressListAdapter
                        adapters.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)


                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                        showProgressDialog(getString(R.string.please_wait))
                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )

                    }

                }
                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }
        } else {
          rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE
        }
    }

    private fun getAddressList(){
//       showProgressDialog(getString(R.string.please_wait))

        shimmer_view_container.startShimmerAnimation()

        FirestoreClass().getAddressList(this)
    }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddressListActivity,
            getString(R.string.error_message_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getAddressList()

    }
}