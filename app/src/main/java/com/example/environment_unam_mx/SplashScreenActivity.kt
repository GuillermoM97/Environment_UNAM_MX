package com.example.environment_unam_mx

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        var globalVar = "Created"
    }

    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)

        handler = Handler()
        handler.postDelayed({
            val intent = Intent(this,SelectDeviceActivity::class.java) //This is the part that
            // initializes the Next Activity
            startActivity(intent)
            finish()
        },4000) // Delaying that amount of miliseconds to open Main Activity

    }
}