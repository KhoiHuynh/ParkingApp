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
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TimePicker
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.bean.SharedViewModel
import com.example.khoi.parkingapp.bean.Spot
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import kotlinx.android.synthetic.main.fragment_add_location.*
import android.arch.lifecycle.ViewModelProviders
import android.widget.Toast
import java.math.BigDecimal
import java.util.*

var spotObj = Spot()
class AddLocationFragment : BaseFragment(){
    private var placeAutocompleteFragment: SupportPlaceAutocompleteFragment? = null
    private var mTimeSetListenerFrom: TimePickerDialog.OnTimeSetListener? = null
    private var mTimeSetListenerTo: TimePickerDialog.OnTimeSetListener? = null
    private var mSeekBar: SeekBar? = null
    private lateinit var model: SharedViewModel

//    private var days = intArrayOf(0,0,0,0,0,0,0)

    companion object {
        private const val TAG = "AddLocationFragment"
        private const val monday = 0
        private const val tuesday = 1
        private const val wednesday = 2
        private const val thursday = 3
        private const val friday = 4
        private const val saturday = 5
        private const val sunday = 6
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
        val default = intArrayOf(0,0,0,0,0,0,0)
        button_next.setOnClickListener{
            if(spotObj.getAddress().isNullOrEmpty()){
                Toast.makeText(activity, "Please enter your spot address", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getDates() == null || Arrays.equals(spotObj.getDates(), default)){
                Toast.makeText(activity, "Please select at least one available day", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getTimeFrom().isNullOrEmpty()){
                Toast.makeText(activity, "Please select an available from time", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getTimeTo().isNullOrEmpty()){
                Toast.makeText(activity, "Please select an available till time", Toast.LENGTH_LONG).show()
            }
            else if(spotObj.getRate() == null || spotObj.getRate() == BigDecimal(0)){
                Toast.makeText(activity, "Please select a rate larger than 0$", Toast.LENGTH_LONG).show()
            }
            else{
                //            spotObj.setDates(intArrayOf(1,1,1,1,1,1,1))
//            spotObj.setDates(days)
                spotObj.printMe()
//            println("my days array: " + Arrays.toString(days) )
//            println("is the days here?" + Arrays.toString(spotObj.getDates()))
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
                spotObj.setAddress(place.name.toString())
            }
            override fun onError(status: Status) {
                Log.i(AddLocationFragment.TAG, "An error occurred: $status")
            }
        })
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
                spotObj.setRate(value.toBigDecimal())
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
    private var arr: IntArray = intArrayOf(0,0,0,0,0,0,0)
    fun onCheckboxClicked(view: View){
        if(view is CheckBox){
            val checked: Boolean = view.isChecked
            when(view.id){
                R.id.checkbox_monday -> {
                    if (checked) {
                        arr[monday] = 1
                    }else{
                        arr[monday] = 0
                    }
                }
                R.id.checkbox_tuesday -> {
                    if (checked) {
                        arr[tuesday] = 1
                    }else{
                        arr[tuesday] = 0
                    }
                }
                R.id.checkbox_wednesday -> {
                    if (checked) {
                        arr[wednesday] = 1
                    }else{
                        arr[wednesday] = 0
                    }
                }
                R.id.checkbox_thursday -> {
                    if (checked) {
                        arr[thursday] = 1
                    }else{
                        arr[thursday] = 0
                    }
                }
                R.id.checkbox_friday -> {
                    if (checked) {
                        arr[friday] = 1
                    }else{
                        arr[friday] = 0
                    }
                }
                R.id.checkbox_saturday -> {
                    if (checked) {
                        arr[saturday] = 1
                    }else{
                        arr[saturday] = 0
                    }
                }
                R.id.checkbox_sunday -> {
                    if (checked) {
                        arr[sunday] = 1
                    }else{
                        arr[sunday] = 0
                    }
                }
            }

//            days = arr.clone()
//            println("clicked: " + Arrays.toString(days))
            spotObj.setDates(arr)
//            println("HELLO PARTH THIS IS THE SPOT OBJECT: " + Arrays.toString(spotObj.getDates()))

        }
    }

}
