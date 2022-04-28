package com.example.my212.utls

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class CKRadioButton(context: Context,attributeSet: AttributeSet):AppCompatRadioButton(context,attributeSet) {
    init {
        applyFont()
    }
    private fun applyFont() {

        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Cat.ttf")
        setTypeface(typeface)
    }
}