package com.example.khoi.parkingapp.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.khoi.parkingapp.R
import kotlinx.android.synthetic.main.activity_rent.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private var address: String? = null
private var description: String? = null
private var rate: String? = null
private var days: ArrayList<Int>? = null
private var fromTime: String? = null
private var toTime: String? = null

class RentActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.toggleButton_monday ->{
                Toast.makeText(this, toggleButton_monday.isChecked.toString(), Toast.LENGTH_LONG).show()
//                toggleButton_monday.isChecked
                return
            }
            R.id.toggleButton_tuesday ->{
                Toast.makeText(this, "Tuesday Clicked", Toast.LENGTH_LONG).show()
                return
            }
        }
    }

    companion object {
        private const val TAG = "RentActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent)
        val intent: Intent = intent
        val bundle: Bundle = intent.extras?.get("bundle") as Bundle
        address = bundle.getString("address")
        description = bundle.getString("description")
        rate = bundle.getString("rate") + "0 $/h"
        days = bundle.getIntegerArrayList("days")
        fromTime = bundle.getString("fromTime")
        toTime = bundle.getString("toTime")

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

        greyOutButtons(days!!)

        Log.d(TAG, "days: " + days)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->{
                onBackPressed()
            }
        }
        return true
    }

}
