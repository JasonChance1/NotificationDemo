package com.example.notificationdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.blankj.utilcode.util.ToastUtils
import com.example.notificationdemo.databinding.ActivityContentBinding
import com.example.notificationdemo.databinding.ActivityMainBinding

class ContentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getStringExtra("data")
        if(!data.isNullOrEmpty()){
            binding.result.text = data
        }else{
            ToastUtils.showLong("无数据")
        }
    }
}