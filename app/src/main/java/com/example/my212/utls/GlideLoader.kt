package com.example.my212.utls

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import android.widget.ImageView
import com.example.my212.R
import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserPicture(image:Any,imageView: ImageView){
        try{
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
    fun loadProductPicture(image : Any, imageView : ImageView){
        try {
            Glide
                .with(context)
                .load(image)
                .centerCrop()
                .into(imageView)

        }catch (e : IOException){
            e.printStackTrace()
        }
    }
}
