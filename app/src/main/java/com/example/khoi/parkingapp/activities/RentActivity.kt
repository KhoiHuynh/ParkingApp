package com.example.khoi.parkingapp.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.khoi.parkingapp.R
import java.util.*


class RentActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "RentActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent)

        val intent: Intent = intent
        val bundle: Bundle = intent.extras?.get("bundle") as Bundle
        val address = bundle.getString("address")
        val description = bundle.getString("description")
        val rate = bundle.getString("rate")
        val days = bundle.getIntegerArrayList("days")
        val fromTime = bundle.getString("fromTime")
        val toTime = bundle.getString("toTime")

        Log.d(TAG, "bundle: " + address)
        Log.d(TAG, "bundle: " + description)
        Log.d(TAG, "bundle: " + rate)
        Log.d(TAG, "bundle: " + Arrays.asList(days))
        Log.d(TAG, "bundle: " + fromTime)
        Log.d(TAG, "bundle: " + toTime)


//        bundle.putString("address", address)
//        bundle.putString("description", description)
//        bundle.putString("rate", rate)
//        bundle.putIntegerArrayList("days", days)
//        bundle.putString("fromTime", fromTime)
//        bundle.putString("toTime", toTime)


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
