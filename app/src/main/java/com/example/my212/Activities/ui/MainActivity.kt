package com.example.my212.Activities.uia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import com.example.my212.R
import com.example.my212.utls.Constants

class MainActivity : AppCompatActivity() {
    lateinit var main:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main=findViewById(R.id.tv_main)

        val SharedPreferences =
            getSharedPreferences(Constants.MYCLOSET_PREFERENCES, android.content.Context.MODE_PRIVATE)
        val username=SharedPreferences.getString(Constants.LOGGED_IN_USERNAME,"")!!
        main.text="Hello $username."
    }
}