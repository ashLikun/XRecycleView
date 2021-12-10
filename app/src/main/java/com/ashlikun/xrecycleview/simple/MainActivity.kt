package com.ashlikun.xrecycleview.simple

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ashlikun.xrecycleview.simple.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }


    fun onClick(view: View) {
        startActivity(Intent(this, TestActivity::class.java))
    }

    fun StaggeredActivity(view: View) {
        startActivity(Intent(this, StaggeredActivity::class.java))
    }

    fun PageTestActivity(view: View) {
        startActivity(Intent(this, PageTestActivity::class.java))
    }
}