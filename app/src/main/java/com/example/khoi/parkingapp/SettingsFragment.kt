package com.example.khoi.parkingapp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class SettingsFragment : Fragment(){
    private val TAG = "SettingsFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_location, container, false)
        val button = view.findViewById(R.id.button) as Button
        button.setOnClickListener{
            Log.d(TAG, "Clicked")
        }
        return view
    }
}
