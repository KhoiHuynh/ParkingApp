package com.example.khoi.parkingapp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
                auth = FirebaseAuth.getInstance()

        btn_register.setOnClickListener {
            performRegister()
        }

        tv_already_have_account.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister(){
        val name = et_name.text.toString()
        val email = et_register_email.text.toString()
        val password = et_register_password.text.toString()
        Log.d(TAG, "Name: $name")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "Password: $password")

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill in Email and Password fields", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Log.d(TAG, "created user with uid: ${it.result?.user?.uid}")
                    Toast.makeText(this, "You account has been registered", Toast.LENGTH_SHORT).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", it.exception)
                    val errorMsg = it.exception?.message.toString()
                    Toast.makeText(baseContext, errorMsg, Toast.LENGTH_SHORT).show()
                    return@addOnCompleteListener
                }
            }
    }
}


