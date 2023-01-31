package com.voitov.andoidservices

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.voitov.andoidservices.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonService.setOnClickListener {
            startService(MyService.newIntent(application, 19))
        }

        binding.buttonForegroundService.setOnClickListener {
            ContextCompat.startForegroundService(this, MyForegroundService.newIntent(this, 30))
        }

        binding.buttonStopForegroundService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
        }
    }
}