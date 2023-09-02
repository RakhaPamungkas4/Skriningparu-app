package com.rakul.skriningparu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rakul.skriningparu.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnBoarding.setOnClickListener {
                val intent = Intent(this@HomeActivity, WebViewActivity::class.java)
                startActivity(intent)
            }
            btnStartScreening.setOnClickListener {
                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}