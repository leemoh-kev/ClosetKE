package com.example.my212.utls

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView


import android.util.AttributeSet

class MSPTextViewBold(context: Context,att:AttributeSet) : AppCompatTextView(context,att) {
    init {
        applyFont()
    }

    private fun applyFont() {

        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "MBold.ttf")
        setTypeface(typeface)
    }

}