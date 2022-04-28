package com.example.my212.Activities.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.my212.Activities.uia.BaseActivity
import com.example.my212.Firestore.FirestoreClass
import com.example.my212.Firestore.models.Address
import com.example.my212.R
import com.example.my212.utls.Constants
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : BaseActivity() {
//    private lateinit var binding : ActivityAddEditAddressBinding
    private lateinit var tooledit:Toolbar
    private var mAddressDetails : Address? = null
    private lateinit var FullName: EditText
    private lateinit var PhoneNumber: EditText
    private lateinit var etAddress: EditText
    private lateinit var ZipCode: EditText
    private lateinit var AdditionalNote: EditText
    private lateinit var OtherDetails: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityAddEditAddressBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_add_edit_address)
        tooledit=findViewById(R.id.toolbar_add_edit_address_activity)
        FullName=findViewById(R.id.et_full_name)
        PhoneNumber=findViewById(R.id.et_phone_number)
        etAddress=findViewById(R.id.et_address)
        ZipCode=findViewById(R.id.et_zip_code)
        AdditionalNote=findViewById(R.id.et_additional_note)
        OtherDetails=findViewById(R.id.et_other_details)

        setUpActionBar()

        btn_submit_address.setOnClickListener{
            saveAddressToFireStore()
            val intent = Intent(this,MyOrderDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS,true)
            startActivity(intent)
            saveAddressToFireStore()
        }

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }
        if(mAddressDetails != null){
            if (mAddressDetails!!.id.isNotEmpty()){
                tv_title.text = getString(R.string.title_edit_address)
                btn_submit_address.text = getString(R.string.btn_lbl_update)

                et_full_name.setText(mAddressDetails?.name)
                et_phone_number.setText(mAddressDetails?.mobileNumber)
                et_zip_code.setText(mAddressDetails?.zipcode)
                et_additional_note.setText(mAddressDetails?.additionalNote)
                et_address.setText(mAddressDetails?.address)

                when(mAddressDetails?.type){
                    Constants.HOME->{
                       rb_home.isChecked = true
                    }
                    Constants.OFFICE ->{
                        rb_office.isChecked = true
                    }else->{
                    rb_other.isChecked = true
                   til_other_details.visibility = View.VISIBLE
                    et_other_details.setText(mAddressDetails?.otherDetails)
                }
                }
            }
        }



        rg_type.setOnCheckedChangeListener{ _,checkedId ->
            if (checkedId ==  R.id.rb_other){
                til_other_details.visibility = View.VISIBLE
            }else{
               til_other_details.visibility = View.GONE
            }
        }
    }
    private fun setUpActionBar(){
        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_button_white_24)
        }
        tooledit.setNavigationOnClickListener { onBackPressed() }
        }
    private fun validateData() : Boolean{

        return when{
            TextUtils.isEmpty(FullName.text.toString().trim { it<= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                false
            }
            TextUtils.isEmpty(PhoneNumber.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter your phone number.",true)
                false
            }
            TextUtils.isEmpty(etAddress.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter your Address.",true)
                false
            }
            TextUtils.isEmpty(ZipCode.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar("Please enter area Zipcode.",true)
                false
            }
            rb_other.isChecked && TextUtils.isEmpty(
                ZipCode.text.toString().trim{ it <= ' '}
            ) -> {
                showErrorSnackBar("Please enter other details.",true)
                false
            }
            else->{
                true
            }
        }

    }
    private fun saveAddressToFireStore(){
        val fullName : String = FullName.text.toString().trim{ it <= ' '}
        val phoneNumber : String = PhoneNumber.text.toString().trim{ it <= ' '}
        val address : String = etAddress.text.toString().trim{ it <= ' '}
        val zipCode : String = ZipCode.text.toString().trim{ it <= ' '}
        val additionalNote : String = AdditionalNote.text.toString().trim{ it <= ' '}
        val otherDetails : String = OtherDetails.text.toString().trim{ it <= ' '}

        if (validateData()){

            showProgressDialog(getString(R.string.please_wait))

            val addressType : String = when{
                rb_home.isChecked-> Constants.HOME
          rb_office.isChecked-> Constants.OFFICE
                else-> Constants.OTHER
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
                FirestoreClass().updateAddressSuccess(this,addressModel,mAddressDetails!!.id)
            }else {

                FirestoreClass().addAddress(this, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess(){
        hideProgressDialog()
        if(mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()){
            Toast.makeText(this, getString(R.string.msg_your_address_updated_successfully), Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(this, getString(R.string.address_added_success_msg), Toast.LENGTH_SHORT)
                .show()
        }
        setResult(Activity.RESULT_OK)
        finish()
    }
    }

