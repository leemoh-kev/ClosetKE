package com.example.my212.utls

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatButton

import android.util.AttributeSet

class MSPButton(context: Context,att:AttributeSet):AppCompatButton(context,att) {
    init {
        applyFont()
    }
    private fun applyFont() {

        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Cat.ttf")
        setTypeface(typeface)
    }

}