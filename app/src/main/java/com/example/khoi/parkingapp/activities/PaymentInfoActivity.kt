package com.example.khoi.parkingapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.khoi.parkingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.stripe.android.SourceCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Source
import com.stripe.android.model.SourceParams
import com.stripe.android.model.Token
import kotlinx.android.synthetic.main.activity_payment_info.*
import java.lang.Exception




class PaymentInfoActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PaymentInfoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_info)

        val card: Card? = card_input_widget.card
        val cardSourceParams: SourceParams = SourceParams.createCardParams(card!!)
        val stripe = Stripe(this@PaymentInfoActivity, "pk_test_luUv7LE0GLSq9YCrJbYmdSPN")

//        stripe.createSource(
//            cardSourceParams,
//            object : SourceCallback{
//                override fun onSuccess(source: Source) {
//                    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
//                    val database = FirebaseDatabase.getInstance()
//                    val pushId = database.getReference("stripe_customers/$currentUser/sources/").push().key
//                    val ref = database.getReference("stripe_customers/$currentUser/sources/$pushId/token/")
//                    //save the token id from the "token" object we received from Stripe
//                    ref.setValue(source)
//                        .addOnSuccessListener {
//                            Log.d(TAG, "Added Stripe Token to database successfully")
//                        }
//                        .addOnFailureListener {
//                            Log.d(TAG, "Failed to add Token to database")
//                        }
//                }
//
//                override fun onError(error: Exception) {
//
//                }
//
//            })

//        val charge = 1550
//        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
//        val database = FirebaseDatabase.getInstance()
//        val pushId = database.getReference("stripe_customers/$currentUser/charges").push().key
//        val ref = database.getReference("stripe_customers/$currentUser/charges/$pushId/amount")
//        //save the token id from the "token" object we received from Stripe
//        ref.setValue(charge)
//            .addOnSuccessListener {
//                Log.d(PaymentInfoActivity.TAG, "Added Stripe charge successfully")
//            }
//            .addOnFailureListener {
//                Log.d(PaymentInfoActivity.TAG, "Stripe charge failed to add to DB")
//            }
    }


}
