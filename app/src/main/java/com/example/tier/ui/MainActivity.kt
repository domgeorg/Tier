package com.example.tier.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tier.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Tier)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
