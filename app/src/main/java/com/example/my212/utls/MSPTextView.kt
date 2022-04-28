package com.example.my212.utls

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextView(context: Context, att: AttributeSet) : AppCompatTextView(context,att) {
    init {
        applyFont()
    }

    private fun applyFont() {

        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "MBold.ttf")
        setTypeface(typeface)
    }
}