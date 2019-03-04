package com.example.khoi.parkingapp.fragments

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.bean.SharedViewModel
import com.example.khoi.parkingapp.bean.Spot
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import android.arch.lifecycle.ViewModelProviders
import android.os.Handler
import android.widget.*
import kotlinx.android.synthetic.main.fragment_add_location.*
import java.util.*

class AddLocationFragment : BaseFragment(){
    var spotObj = Spot()

    private var placeAutocompleteFragment: SupportPlaceAutocompleteFragment? = null
    private var mTimeSetListenerFrom: TimePickerDialog.OnTimeSetListener? = null
    private var mTimeSetListenerTo: TimePickerDialog.OnTimeSetListener? = null
    private lateinit var model: SharedViewModel
    private var tempPlace: Place? = null

    companion object {
        private val TAG = AddLocationFragment::class.qualifiedName
        fun newInstance(instance: Int): AddLocationFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = AddLocationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_location, container, false)
        val fm: FragmentManager = childFragmentManager
        placeAutocompleteFragment = fm.findFragmentByTag("placeAutocompleteFragment") as SupportPlaceAutocompleteFragment?
        if (placeAutocompleteFragment == null){
            placeAutocompleteFragment = SupportPlaceAutocompleteFragment()
            fm.beginTransaction().add(R.id.address_layout, placeAutocompleteFragment!!, "placeAutocompleteFragment").commit()
//            placeAutocompleteFragment.setHint("Hello")
            fm.executePendingTransactions()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        getAutoCompleteSearchResults()
        setFromAndToTime()
        setSeekBar()
        Handler().postDelayed({
            clearButton()
        }, 100)
        button_next.setOnClickListener{
            checkDays()
            spotObj.setPlace(tempPlace)

            if(spotObj.getPlace() == null){
                Toast.makeText(activity, "Please enter your spot address", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getDays() == null || spotObj.getDays() == listOf(0,0,0,0,0,0,0)){
                Toast.makeText(activity, "Please select at least one available day", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getTimeFrom().isNullOrEmpty()){
                Toast.makeText(activity, "Please select an available from time", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getTimeTo().isNullOrEmpty()){
                Toast.makeText(activity, "Please select an available till time", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getRate() == null || spotObj.getRate() == "0.0"){
                Toast.makeText(activity, "Please select a rate larger than 0$", Toast.LENGTH_LONG).show()
            }
            else{

                model.spot.postValue(spotObj)
                mFragmentNavigation.pushFragment(Host2Fragment.newInstance(0))
            }
        }
    }

    private fun getAutoCompleteSearchResults(){
        placeAutocompleteFragment!!.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(AddLocationFragment.TAG, "Place: " + place.name)
                tempPlace = place
            }
            override fun onError(status: Status) {
                Log.i(AddLocationFragment.TAG, "An error occurred: $status")
            }
        })
    }

    private fun clearButton() {
        placeAutocompleteFragment?.view?.findViewById<View>(R.id.place_autocomplete_clear_button)?.setOnClickListener {
            Log.d(TAG, "Cleared Button Clicked")
            placeAutocompleteFragment?.setText("")
            it.visibility = View.GONE
            tempPlace = null
        }
    }

    private fun setSeekBar(){
        sb_rate.max = 80
        sb_rate.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val progressFloat: Float = progress.toFloat()
                val value = (progressFloat / 10.00).toFloat()
                val text = value.toString() + "0 $/h"
                tv_rate.text = text
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val progressFloat: Float = seekBar.progress.toFloat()
                val value = (progressFloat / 10.00).toFloat()
                spotObj.setRate(value.toString())
            }
        })
    }

    private fun setFromAndToTime(){
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val is24Hour = false
        var amPM: String

        tv_from_time.setOnClickListener {
            val dialog = TimePickerDialog(
                activity!!,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mTimeSetListenerFrom,
                hour, minute, is24Hour
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
        tv_to_time.setOnClickListener {
            val dialog = TimePickerDialog(
                activity!!,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mTimeSetListenerTo,
                hour, minute, is24Hour
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }

        mTimeSetListenerFrom = TimePickerDialog.OnTimeSetListener { _: TimePicker, hours: Int, minutes: Int ->
            var displayHour: Int
            val displayMinutes: String

            if (hours < 12) {
                amPM = "AM"
                displayHour = hours
            } else {
                amPM = "PM"
                displayHour = hours - 12
                if( displayHour == 0){
                    displayHour = 12
                }
            }
            if(minutes < 10){
                displayMinutes = "0$minutes"
            }else{
                displayMinutes = minutes.toString()
            }
            Log.d(AddLocationFragment.TAG, "minutes: $displayMinutes")
            val time : String
            time = "$displayHour:$displayMinutes $amPM"
            tv_from_time.text = time
            spotObj.setTimeFrom(time)
        }
        mTimeSetListenerTo = TimePickerDialog.OnTimeSetListener { _: TimePicker, hours: Int, minutes: Int ->
            var displayHour: Int
            val displayMinutes: String

            if (hours < 12) {
                amPM = "AM"
                displayHour = hours
            } else {
                amPM = "PM"
                displayHour = hours - 12
                if( displayHour == 0){
                    displayHour = 12
                }
            }
            if(minutes < 10){
                displayMinutes = "0$minutes"
            }else{
                displayMinutes = minutes.toString()
            }
            val time : String
            time = "$displayHour:$displayMinutes $amPM"
            tv_to_time.text = time
            spotObj.setTimeTo(time)

        }
    }

    private fun checkDays(){
        val arr = arrayListOf(0,0,0,0,0,0,0)
        if(checkbox_monday.isChecked){
            arr[0] = 1
        }
        else{
            arr[0] = 0
        }
        if(checkbox_tuesday.isChecked){
            arr[1] = 1
        }
        else{
            arr[1] = 0
        }
        if(checkbox_wednesday.isChecked){
            arr[2] = 1
        }
        else{
            arr[2] = 0
        }
        if(checkbox_thursday.isChecked){
            arr[3] = 1
        }
        else{
            arr[3] = 0
        }
        if(checkbox_friday.isChecked){
            arr[4] = 1
        }
        else{
            arr[4] = 0
        }
        if(checkbox_saturday.isChecked){
            arr[5] = 1
        }
        else{
            arr[5] = 0
        }
        if(checkbox_sunday.isChecked){
            arr[6] = 1
        }
        else{
            arr[6] = 0
        }
        spotObj.setDays(arr)
    }
}
