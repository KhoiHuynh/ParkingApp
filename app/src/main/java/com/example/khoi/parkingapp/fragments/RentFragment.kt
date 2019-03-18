package com.example.khoi.parkingapp.fragments


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.example.khoi.parkingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import kotlinx.android.synthetic.main.fragment_rent.*
import java.lang.Exception
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.stripe.android.model.*


class RentFragment : BaseFragment(), View.OnClickListener {
    private var address: String? = null
    private var description: String? = null
    private var rate: String? = null
    private var days: ArrayList<Int>? = null
    private var fromTime: String? = null
    private var toTime: String? = null
    private var spotId: String? = null
    lateinit var radiogrp: RadioGroup
    private var radioButtonMap: HashMap<RadioButton, DataSnapshot> = HashMap()
    private var strLast4 = ""
    private var selectedDays: ArrayList<Int> = arrayListOf(0,0,0,0,0,0,0)
    private var charge: Int = 0
    private var roundOffCharge: Float = 0f
    private lateinit var bitmapdraw: BitmapDrawable
    private lateinit var b: Bitmap
    private lateinit var rentedMarker: Bitmap

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.toggleButton_monday ->{
                if(toggleButton_monday.isChecked){
                    selectedDays[0] = 1
                }
                else{
                    selectedDays[0] = 0
                }
                return
            }
            R.id.toggleButton_tuesday ->{
                if(toggleButton_tuesday.isChecked){
                    selectedDays[1] = 1
                }
                else{
                    selectedDays[1] = 0
                }
                return
            }
            R.id.toggleButton_wednesday ->{
                if(toggleButton_wednesday.isChecked){
                    selectedDays[2] = 1
                }
                else{
                    selectedDays[2] = 0
                }
                return
            }
            R.id.toggleButton_thursday ->{
                if(toggleButton_thursday.isChecked){
                    selectedDays[3] = 1
                }
                else{
                    selectedDays[3] = 0
                }
                return
            }
            R.id.toggleButton_friday ->{
                if(toggleButton_friday.isChecked){
                    selectedDays[4] = 1
                }
                else{
                    selectedDays[4] = 0
                }
                return
            }
            R.id.toggleButton_saturday ->{
                if(toggleButton_saturday.isChecked){
                    selectedDays[5] = 1
                }
                else{
                    selectedDays[5] = 0
                }
                return
            }
            R.id.toggleButton_sunday ->{
                if(toggleButton_sunday.isChecked){
                    selectedDays[6] = 1
                }
                else{
                    selectedDays[6] = 0
                }
                return
            }
        }
    }

    @SuppressLint("NewApi")
    private var viewId = View.generateViewId()
    companion object {
        private const val TAG = "RentFragment"
        fun newInstance(instance: Int): RentFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = RentFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_rent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bitmapdraw = ContextCompat.getDrawable(context!!,R.drawable.ic_grey_marker) as BitmapDrawable
        b = bitmapdraw.bitmap
        rentedMarker = Bitmap.createScaledBitmap(b,65, 92, false)

        setupUI()


        button_rent.setOnClickListener {
            openDialog()
        }

        button2_pay.setOnClickListener {
            pay()
        }
    }
    lateinit var ll: RadioGroup
    @SuppressLint("NewApi")
    private fun addRadioButtons() {
        val database = FirebaseDatabase.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val query = database.getReference("stripe_customers/$currentUser/sources/")
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){

                    var counter = 1
                    for (source: DataSnapshot in dataSnapshot.children) {
                        val last4 = source.child("last4").value.toString()
                        val brand = source.child("brand").value.toString()
                        Log.d(TAG, "last4: " + last4)

                        val rdbtn = RadioButton(context)
                        rdbtn.id = viewId + counter
                        Log.d(TAG, (viewId + counter).toString())

                        val tag = "card_$counter"
                        rdbtn.tag =tag

                        val textStr = "$brand ************$last4"
                        rdbtn.text = textStr

                        ll.addView(rdbtn)
                        counter++
                        radioButtonMap.put(rdbtn, source)
                        Log.d(TAG, "TAG IS: ${rdbtn.tag}")

                    }
                    radiogrp.addView(ll)
                }
            }

        })
//
//        for (row in 0..0) {
//            val ll = RadioGroup(context)
//
//            for (i in 1..number) {
//                val rdbtn = RadioButton(context)
//                rdbtn.id = View.generateViewId()
//                rdbtn.text = "Radio " + rdbtn.id
//                ll.addView(rdbtn)
//            }
//            radiogrp.addView(ll)
//        }
    }

    private fun setupUI(){
        val bundle: Bundle = this.arguments!!
        address = bundle.getString("address")
        description = bundle.getString("description")
        rate = bundle.getString("rate")!! + "0 $/h"
        days = bundle.getIntegerArrayList("days")
        fromTime = bundle.getString("fromTime")
        toTime = bundle.getString("toTime")
        spotId = bundle.getString("spotId")

        Log.d(TAG, "myBundle $address")
        Log.d(TAG, "myBundle $description")
        Log.d(TAG, "myBundle $rate")
        Log.d(TAG, "myBundle $days")
        Log.d(TAG, "myBundle $fromTime")
        Log.d(TAG, "myBundle $toTime")


        //setting up all the textviews
        if(description.isNullOrBlank()){
            textView9_description.text = "Description not available"
        }
        else{
            textView9_description.text = description
        }
        textView7_address.text = address
        textView8_rate.text = rate
        tv_rent_time_from.text = fromTime
        tv_rent_time_to.text = toTime

        //setting up all the ToggleButtons
        toggleButton_monday.setOnClickListener(this)
        toggleButton_tuesday.setOnClickListener(this)
        toggleButton_wednesday.setOnClickListener(this)
        toggleButton_thursday.setOnClickListener(this)
        toggleButton_friday.setOnClickListener(this)
        toggleButton_saturday.setOnClickListener(this)
        toggleButton_saturday.setOnClickListener(this)

        //grey out and disable day buttons that aren't available
        greyOutButtons(days!!)

        //range seekbar
        val earliestTime = convertStringTimeToMin(fromTime!!)
        val latestTime = convertStringTimeToMin(toTime!!)
        rangeSeekbar.setMinValue(earliestTime.toFloat())
        rangeSeekbar.setMaxValue(latestTime.toFloat())

        // we want a gap of 15 minutes minimum, so we need to find the percentage of the time different of the
        // latestTime minus earliestTime.
        //ex: the time difference between latest and earliest is 60min
        //    we want a 15minute gap, so 15min of 60min is 25%. 25 is the number we use to set the gap
        val timeDiff: Float = (latestTime - earliestTime).toFloat()
        val gapInMinutes = 15.0f
        val gapPercentage: Float = (gapInMinutes/timeDiff)*100
        rangeSeekbar.setGap(gapPercentage)
        rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->

            val timeRange = "${convertMinToStrTime(minValue.toInt())} - ${convertMinToStrTime(maxValue.toInt())}"
            tv_rent_time_range.text = timeRange
//            tv_rent_time_range.text = minValue.toString() + " - " + maxValue.toString()
            val totalRentTime: Int = (maxValue.toInt() - minValue.toInt())
            val totalCost = ((totalRentTime * bundle.getString("rate")!!.toFloat())/ 60)
            roundOffCharge = (Math.round(totalCost * 100.0)/100.0).toFloat()
            val temp: String
            if(roundOffCharge.toString().substringAfter(".").length == 1){
                temp = "Total: " + roundOffCharge + "0$"
            }
            else{
                temp = "Total: $roundOffCharge$"
            }
            tv_rent_total_cost.text = temp
        }

        rangeSeekbar.setOnRangeSeekbarFinalValueListener { minValue, maxValue ->
            println("User is renting from: ${convertMinToStrTime(minValue.toInt())} until ${convertMinToStrTime(maxValue.toInt())}")
        }

        //payment
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val query = FirebaseDatabase.getInstance().getReference("stripe_customers/$currentUser/sources/").limitToFirst(1)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    var temp = ""
                    for(cardInfo:DataSnapshot in dataSnapshot.children){
                        temp = "VISA ${cardInfo.child("last4").value.toString()}"
                    }
                    button_rent.text = temp
                }
            }

        })
    }

    private fun openDialog() {
        val dialog = Dialog(this.context!!)

        dialog.setContentView(R.layout.stripe_layout)
        val lp : WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
            copyFrom(dialog.window?.attributes)
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }
        ll = RadioGroup(context)
        radiogrp = dialog.findViewById<View>(R.id.radio_group) as RadioGroup
        val submit = dialog.findViewById<View>(R.id.submit) as TextView
        val cardNo = dialog.findViewById<View>(R.id.cardNo) as EditText
        val month = dialog.findViewById<View>(R.id.month) as EditText
        val year = dialog.findViewById<View>(R.id.year) as EditText
        val cvv = dialog.findViewById<View>(R.id.cvv) as EditText
        addRadioButtons()

        ll.setOnCheckedChangeListener { _, checkedId ->
            val btn = radiogrp.findViewById<RadioButton>(checkedId)
            val source = radioButtonMap.get(btn)
            val something = source?.child("fingerprint")?.value.toString()
            Log.d(TAG, "PLEASE $something")

        }

        submit.setOnClickListener {
            when {
                cardNo.length() == 0 || month.length() == 0 || year.length() == 0 || cvv.length() == 0 ->
                    Toast.makeText(activity, "Please fill all the fields"
                        , Toast.LENGTH_SHORT).show()


                cardNo.length() < 16 -> Toast.makeText(activity, "Please enter" +
                        " valid Card No.", Toast.LENGTH_SHORT).show()
                else -> {
                    validateCard(cardNo.text.toString(), month.text.toString(), year.text.toString(), cvv.text.toString())
                    strLast4 = cardNo.text.toString().takeLast(4)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
        dialog.window?.attributes = lp

    }
    private fun validateCard(card: String?, month: String?, year: String?, cvv: String?) {
        val stripe = Stripe(this.context!!, "pk_test_luUv7LE0GLSq9YCrJbYmdSPN")

        val cardToSave = Card(card, Integer.valueOf(month!!), Integer.valueOf(year!!), cvv)
        cardToSave.currency = "USD"
        stripe.createToken(cardToSave, object : TokenCallback {
            override fun onSuccess(token: Token?) {
                Log.v("Token!","Token Created!!"+ token!!.id)
                Toast.makeText(activity, "Payment Method Added!", Toast.LENGTH_SHORT).show()
                addCard(token.id)
            }

            override fun onError(error: Exception?) {
                Toast.makeText(activity, error!!.message, Toast.LENGTH_SHORT).show()
                error.printStackTrace()
            }

        })
    }

    private fun pay(){
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val amountPushId = database.getReference("stripe_customers/$currentUser/charges").push().key
        val amountRef = database.getReference("stripe_customers/$currentUser/charges/$amountPushId/amount")

        if(selectedDays == arrayListOf(0,0,0,0,0,0,0)){
            Log.d(TAG, "selected days: $selectedDays")
            Toast.makeText(activity, "Please select at least one day", Toast.LENGTH_LONG).show()
        }
        else{
            charge = (roundOffCharge*100).toInt()
            Log.d(TAG, "charge: $charge")
            amountRef.setValue(charge)
                .addOnSuccessListener {
                    Log.d(TAG, "Added Stripe charge successfully")
                    Toast.makeText(activity, "Transaction Completed", Toast.LENGTH_LONG).show()
                    FirebaseDatabase.getInstance().getReference("/spots/$spotId/availability")
                        .setValue("rented")
                    val rentedSpot = markerMap2.get(spotId)
                    rentedSpot?.setIcon(BitmapDescriptorFactory.fromBitmap(rentedMarker))
                    mFragmentNavigation.clearStack()
                }
                .addOnFailureListener {
                    Log.d(TAG, "Stripe charge failed to add to DB")
                    Toast.makeText(activity, "Transaction Failed. Please try again later", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun addCard(token: String?) {
        // Pass that token, amount to your server using API to process payment.
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseDatabase.getInstance()
        val pushId = database.getReference("stripe_customers/$currentUser/sources/").push().key
        val ref = database.getReference("stripe_customers/$currentUser/sources/$pushId/token/")
        //save the token id from the "token" object we received from Stripe
        ref.setValue(token)
            .addOnSuccessListener {
                Log.d(RentFragment.TAG, "Added Stripe Token to database successfully")
                val temp = "VISA $strLast4"
                button_rent.text = temp
            }
            .addOnFailureListener {
                Log.d(RentFragment.TAG, "Failed to add Token to database")
            }
    }

    private fun convertStringTimeToMin(strTime: String): Int{
        var tempStrTime = strTime
        val zone = strTime.takeLast(2)
        tempStrTime = tempStrTime.substring(0, tempStrTime.length-2).trimEnd()
        val hours = tempStrTime.substringBefore(":").toInt()
        val minutes = tempStrTime.substringAfter(":").toInt()
        var totalMinutes = (hours * 60) + (minutes)

        if(zone == "PM"){
            totalMinutes += 720
        }

        return totalMinutes
    }

    private fun convertMinToStrTime(minutes: Int): String{
        var zone = "AM"
        var hours = minutes / 60
        var mins: String = ""

        if((minutes%60) < 10){
            mins += "0${minutes%60}"
        }
        else{
            mins += (minutes%60)
        }

        if(hours > 12){
            hours %= 12
            zone = "PM"
        }

        return "$hours:$mins $zone"
    }

    private fun greyOutButtons(days: ArrayList<Int>){
        if(days[0] == 0){
            toggleButton_monday.alpha = .5f
            toggleButton_monday.isClickable = false
        }
        if(days[1] == 0){
            toggleButton_tuesday.alpha = .5f
            toggleButton_tuesday.isClickable = false
        }
        if(days[2] == 0){
            toggleButton_wednesday.alpha = .5f
            toggleButton_wednesday.isClickable = false
        }
        if(days[3] == 0){
            toggleButton_thursday.alpha = .5f
            toggleButton_thursday.isClickable = false
        }
        if(days[4] == 0){
            toggleButton_friday.alpha = .5f
            toggleButton_friday.isClickable = false
        }
        if(days[5] == 0){
            toggleButton_saturday.alpha = .5f
            toggleButton_saturday.isClickable = false
        }
        if(days[6] == 0){
            toggleButton_sunday.alpha = .5f
            toggleButton_sunday.isClickable = false
        }
    }


}
