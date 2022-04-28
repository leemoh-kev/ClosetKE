package com.example.my212.utls

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatEditText
import android.util.AttributeSet

class MSPEditText(context: Context,att:AttributeSet) :AppCompatEditText(context,att) {

    init {
        applyFont()
    }
    private fun applyFont() {

        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "MBold.ttf")
        setTypeface(typeface)
    }

}