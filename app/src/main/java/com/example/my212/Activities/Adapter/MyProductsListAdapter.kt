package com.example.my212.Activities.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.my212.Activities.fragments.ProductsFragment
import com.example.my212.Activities.ui.ProductDetailsActivity
import com.example.my212.Firestore.models.Product
import com.example.my212.R
import com.example.my212.databinding.ItemListLayoutBinding
import com.example.my212.utls.Constants
import com.example.my212.utls.GlideLoader

open class MyProductsListAdapter (
    private val context : Context,
    private val list : ArrayList<Product>,
    private val fragment : ProductsFragment
) : RecyclerView.Adapter<MyProductsListAdapter.ViewHolder>(){
    class ViewHolder(val binding : ItemListLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list_layout,parent,false)
        return ViewHolder(ItemListLayoutBinding.bind(view))

    }

//    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        GlideLoader(context).loadProductPicture(model.image,holder.binding.ivItemImage)

        holder.binding.tvItemName.text = model.title
        holder.binding.tvItemPrice.text = "Ksh. ${model.price}"

        holder.binding.ibDeleteProduct.setOnClickListener{
            fragment.deleteProduct(model.product_id)
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID,model.product_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,model.user_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}