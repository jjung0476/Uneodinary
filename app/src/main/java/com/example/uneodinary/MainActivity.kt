package com.example.uneodinary

import android.content.Intent
import android.os.Bundle
import com.example.uneodinary.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import android.app.Activity
import com.example.uneodinary.ui.theme.UneodinaryTheme

class MainActivity : AppCompatActivity() { // ComponentActivity()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UneodinaryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UneodinaryTheme {
        Greeting("Android")
    }
}