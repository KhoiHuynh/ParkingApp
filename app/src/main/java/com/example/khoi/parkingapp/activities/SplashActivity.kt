package com.example.khoi.parkingapp.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    //  Declare an instance of FirebaseAuth.
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()
    }

    // When initializing the Activity, check to see if the user is currently signed in.
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}