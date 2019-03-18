package com.example.khoi.parkingapp.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.fragments.PaymentFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_rent.*
import kotlin.collections.ArrayList
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import java.lang.Exception



class RentActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
//    private var address: String? = null
//    private var description: String? = null
//    private var rate: String? = null
//    private var days: ArrayList<Int>? = null
//    private var fromTime: String? = null
//    private var toTime: String? = null
//    companion object {
//        private const val TAG = "RentActivity"
//    }
//
//    override fun onClick(v: View?) {
//        when(v?.id){
//            R.id.toggleButton_monday ->{
////                Toast.makeText(this, toggleButton_monday.isChecked.toString(), Toast.LENGTH_LONG).show()
//                return
//            }
//            R.id.toggleButton_tuesday ->{
////                Toast.makeText(this, "Tuesday Clicked", Toast.LENGTH_LONG).show()
//                return
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_rent)
////        setupUI()
////        button_rent.setOnClickListener{
////            OpenDialog()
//            val manager = supportFragmentManager
//            val transaction = manager.beginTransaction()
//            transaction.add(R.id.payment_container, PaymentFragment(), "PaymentFragment")
//            transaction.addToBackStack(null)
//            transaction.commit()
//        }
////            setupStripePayment()
////            val charge = 1550
////            val currentUser = FirebaseAuth.getInstance().currentUser?.uid
////            val database = FirebaseDatabase.getInstance()
//////            val pushId = database.getReference("stripe_customers/$currentUser/charges/").push().key
////            val pushId = database.getReference("stripe_customers/$currentUser/charges").push().key
////            val ref = database.getReference("stripe_customers/$currentUser/charges/$pushId/amount")
////            ref.setValue(charge)
////                .addOnSuccessListener {
////                    Log.d(TAG, "Added Stripe charge successfully")
////                }
////                .addOnFailureListener {
////                    Log.d(TAG, "Stripe charge failed to add to DB")
////                }
////        }
////    }
//
////    private fun OpenDialog() {
////
////        var dialog = Dialog(this)
////        dialog.setContentView(R.layout.stripe_layout)
////        var lp : WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
////            copyFrom(dialog.window.attributes)
////            width = WindowManager.LayoutParams.MATCH_PARENT
////            height = WindowManager.LayoutParams.WRAP_CONTENT
////        }
////
////        val submit = dialog.findViewById<View>(R.id.submit) as TextView
////        val cardNo = dialog.findViewById<View>(R.id.cardNo) as EditText
////        val month = dialog.findViewById<View>(R.id.month) as EditText
////        val year = dialog.findViewById<View>(R.id.year) as EditText
////        val cvv = dialog.findViewById<View>(R.id.cvv) as EditText
////
////        submit.setOnClickListener {
////            when {
////                cardNo.length() == 0 || month.length() == 0 || year.length() == 0 || cvv.length() == 0 ->
////                    Toast.makeText(this@RentActivity, "Please fill all the fields"
////                        , Toast.LENGTH_SHORT).show()
////                cardNo.length() < 16 -> Toast.makeText(this@RentActivity, "Please enter" +
////                        " valid Card No.", Toast.LENGTH_SHORT).show()
////                else -> {
////                    validateCard(cardNo.text.toString(), month.text.toString(), year.text.toString(), cvv.text.toString())
////                    dialog.dismiss()
////                }
////            }
////        }
////        dialog.show()
////        dialog.window?.attributes = lp
////    }
//
////    private fun validateCard(card: String?, month: String?, year: String?, cvv: String?) {
////        val stripe: Stripe = Stripe(this@RentActivity, "pk_test_luUv7LE0GLSq9YCrJbYmdSPN")
////        val cardToSave = Card(card, Integer.valueOf(month!!), Integer.valueOf(year!!), cvv)
////        cardToSave.currency = "USD"
////        stripe.createToken(cardToSave, object : TokenCallback {
////            override fun onSuccess(token: Token?) {
////                Log.v("Token!","Token Created!!"+ token!!.getId())
////                Toast.makeText(this@RentActivity, "Token Created!!", Toast.LENGTH_SHORT).show()
//////                chargeCard(token.id)
////            }
////
////            override fun onError(error: Exception?) {
////                Toast.makeText(this@RentActivity, error!!.message, Toast.LENGTH_SHORT).show()
////                error.printStackTrace()
////            }
////
////        })
////    }
//
////    private fun chargeCard(token: String?) {
////        // Pass that token, amount to your server using API to process payment.
////         val currentUser = FirebaseAuth.getInstance().currentUser?.uid
////         val database = FirebaseDatabase.getInstance()
////         val pushId = database.getReference("stripe_customers/$currentUser/sources/").push().key
////         val ref = database.getReference("stripe_customers/$currentUser/sources/$pushId/token/")
////         //save the token id from the "token" object we received from Stripe
////         ref.setValue(token)
////             .addOnSuccessListener {
////                 Log.d(TAG, "Added Stripe Token to database successfully")
////             }
////             .addOnFailureListener {
////                 Log.d(TAG, "Failed to add Token to database")
////             }
////    }
//
//
////    private fun setupStripePayment(){
////        val cardToSave = card_input_widget.card
////        if(cardToSave == null){
////            Toast.makeText(this, "Invalid Card Data", Toast.LENGTH_LONG).show()
////        }
////        cardToSave?.setName("Customer Name")
////
////
////        if (cardToSave != null) {
////            stripe.createToken(
////                cardToSave,
////                 object:TokenCallback {
////                     override fun onSuccess(token: Token?) {
////                         val currentUser = FirebaseAuth.getInstance().currentUser?.uid
////                         val database = FirebaseDatabase.getInstance()
////                         val pushId = database.getReference("stripe_customers/$currentUser/sources/").push().key
////                         val ref = database.getReference("stripe_customers/$currentUser/sources/$pushId/token/")
////                         //save the token id from the "token" object we received from Stripe
////                         ref.setValue(token?.id)
////                             .addOnSuccessListener {
////                                 Log.d(TAG, "Added Stripe Token to database successfully")
////                             }
////                             .addOnFailureListener {
////                                 Log.d(TAG, "Failed to add Token to database")
////                             }
////                     }
////
////                     override fun onError(error: Exception?) {
////                         // Show localized error message
////                         if (error != null) {
////                             Toast.makeText(
////                                 this@RentActivity, error.message, Toast.LENGTH_LONG).show()
////                         }
////                     }
////                 }
////            )
////        }
////    }
//
//
//    private fun convertStringTimeToMin(strTime: String): Int{
//        var tempStrTime = strTime
//        val zone = strTime.takeLast(2)
//        tempStrTime = tempStrTime.substring(0, tempStrTime.length-2).trimEnd()
//        val hours = tempStrTime.substringBefore(":").toInt()
//        val minutes = tempStrTime.substringAfter(":").toInt()
//        var totalMinutes = (hours * 60) + (minutes)
//
//        if(zone == "AM"){
//            totalMinutes -= 720
//        }
//
//        return totalMinutes
//    }
//
//    private fun convertMinToStrTime(minutes: Int): String{
//        var zone = "AM"
//        var hours = minutes / 60
//        var mins: String = ""
//
//        if((minutes%60) < 10){
//            mins += "0${minutes%60}"
//        }
//        else{
//            mins += (minutes%60)
//        }
//
//        if(hours > 12){
//            hours %= 12
//            zone = "PM"
//        }
//
//        return "$hours:$mins $zone"
//    }
//
////    private fun setupUI(){
////        val intent: Intent = intent
////        val bundle: Bundle = intent.extras?.get("bundle") as Bundle
////        address = bundle.getString("address")
////        description = bundle.getString("description")
////        rate = bundle.getString("rate") + "0 $/h"
////        days = bundle.getIntegerArrayList("days")
////        fromTime = bundle.getString("fromTime")
////        toTime = bundle.getString("toTime")
////
////        //setting up all the textviews
////        if(description.isNullOrBlank()){
////            textView9_description.text = "Description not available"
////        }
////        else{
////            textView9_description.text = description
////        }
////        textView7_address.text = address
////        textView8_rate.text = rate
////        tv_rent_time_from.text = fromTime
////        tv_rent_time_to.text = toTime
////
////        //setting up all the ToggleButtons
////        toggleButton_monday.setOnClickListener(this)
////        toggleButton_tuesday.setOnClickListener(this)
////        toggleButton_wednesday.setOnClickListener(this)
////        toggleButton_thursday.setOnClickListener(this)
////        toggleButton_friday.setOnClickListener(this)
////        toggleButton_saturday.setOnClickListener(this)
////        toggleButton_saturday.setOnClickListener(this)
////
////        //grey out and disable day buttons that aren't available
////        greyOutButtons(days!!)
////
////        //range seekbar
////        val earliestTime = convertStringTimeToMin(fromTime!!)
////        val latestTime = convertStringTimeToMin(toTime!!)
////        rangeSeekbar.setMinValue(earliestTime.toFloat())
////        rangeSeekbar.setMaxValue(latestTime.toFloat())
////
////        // we want a gap of 15 minutes minimum, so we need to find the percentage of the time different of the
////        // latestTime minus earliestTime.
////        //ex: the time difference between latest and earliest is 60min
////        //    we want a 15minute gap, so 15min of 60min is 25%. 25 is the number we use to set the gap
////        val timeDiff: Float = (latestTime - earliestTime).toFloat()
////        val gapInMinutes = 15.0f
////        val gapPercentage: Float = (gapInMinutes/timeDiff)*100
////        rangeSeekbar.setGap(gapPercentage)
////        rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
////
////        val timeRange = "${convertMinToStrTime(minValue.toInt())} - ${convertMinToStrTime(maxValue.toInt())}"
////        tv_rent_time_range.text = timeRange
////        }
////
////        rangeSeekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
////            println("User is renting from: ${convertMinToStrTime(minValue.toInt())} until ${convertMinToStrTime(maxValue.toInt())}")
////        }
////    }
////
////    private fun greyOutButtons(days: ArrayList<Int>){
////        if(days[0] == 0){
////            toggleButton_monday.alpha = .5f
////            toggleButton_monday.isClickable = false
////        }
////        if(days[1] == 0){
////            toggleButton_tuesday.alpha = .5f
////            toggleButton_tuesday.isClickable = false
////        }
////        if(days[2] == 0){
////            toggleButton_wednesday.alpha = .5f
////            toggleButton_wednesday.isClickable = false
////        }
////        if(days[3] == 0){
////            toggleButton_thursday.alpha = .5f
////            toggleButton_thursday.isClickable = false
////        }
////        if(days[4] == 0){
////            toggleButton_friday.alpha = .5f
////            toggleButton_friday.isClickable = false
////        }
////        if(days[5] == 0){
////            toggleButton_saturday.alpha = .5f
////            toggleButton_saturday.isClickable = false
////        }
////        if(days[6] == 0){
////            toggleButton_sunday.alpha = .5f
////            toggleButton_sunday.isClickable = false
////        }
////    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home ->{
//                onBackPressed()
//            }
//        }
//        return true
//    }

}
