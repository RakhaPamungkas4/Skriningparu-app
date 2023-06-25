package com.rakul.skriningparu.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rakul.skriningparu.HomeActivity
import com.rakul.skriningparu.databinding.ActivityResultScreeningBinding

class ResultScreeningActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultScreeningBinding

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ResultScreeningActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScreeningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            tvDescription.text =
                "Selamat, Hasil Skrining anda menunjukan anda diduga negatif TB paru"
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this@ResultScreeningActivity, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        super.onBackPressed()
    }
}