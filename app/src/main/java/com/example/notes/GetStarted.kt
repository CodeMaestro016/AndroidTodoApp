package com.example.notes

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GetStarted : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_get_started)

        val getStarted = findViewById<Button>(R.id.getStartedButton)
        getStarted.setOnClickListener{
            val Intent = Intent(this,MainActivity::class.java)
            startActivity(Intent)
        }
    }
}