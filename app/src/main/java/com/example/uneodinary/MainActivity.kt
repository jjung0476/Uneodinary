package com.example.uneodinary

import android.content.Intent
import android.os.Bundle
import com.example.uneodinary.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.app.Activity
import androidx.fragment.app.*
import com.example.uneodinary.ui.theme.UneodinaryTheme

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.main_fragmentContainer, TagFragment())
                    .addToBackStack(null)
            }
        }
    }
}
