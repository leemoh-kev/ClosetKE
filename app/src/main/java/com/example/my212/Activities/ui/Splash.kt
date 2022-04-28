package com.example.my212.Activities.uia

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager.LayoutParams.*
import android.widget.TextView
import com.example.my212.R

class Splash : AppCompatActivity() {
    lateinit var tvappname:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        tvappname=findViewById(R.id.tv_app_name)


        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else{
            window.setFlags(
                FLAG_FULLSCREEN,
                FLAG_FULLSCREEN
            )
        }
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                startActivity(Intent(this@Splash, LoginActivity::class.java))
                finish()
            },
            1500
        )

        val typeface: Typeface = Typeface.createFromAsset(assets, "Cat.ttf")
        tvappname.typeface=typeface

    }

}