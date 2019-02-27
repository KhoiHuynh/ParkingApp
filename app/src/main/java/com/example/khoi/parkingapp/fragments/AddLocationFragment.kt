package com.example.khoi.parkingapp.fragments

import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TimePicker
import com.example.khoi.parkingapp.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
import kotlinx.android.synthetic.main.fragment_add_location.*
import kotlinx.android.synthetic.main.fragment_add_location.view.*
import java.util.*

class AddLocationFragment : BaseFragment(){
    private var mTimeSetListenerFrom: TimePickerDialog.OnTimeSetListener? = null
    private var mTimeSetListenerTo: TimePickerDialog.OnTimeSetListener? = null
    private var mSeekBar: SeekBar? = null
    private val date = intArrayOf(0,0,0,0,0,0,0)
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
        var placeAutocompleteFragment: SupportPlaceAutocompleteFragment? = fm.findFragmentByTag("placeAutocompleteFragment") as SupportPlaceAutocompleteFragment?

        if (placeAutocompleteFragment == null){
            placeAutocompleteFragment = SupportPlaceAutocompleteFragment()
            fm.beginTransaction().add(R.id.address_layout, placeAutocompleteFragment, "placeAutocompleteFragment").commit()
//            placeAutocompleteFragment.setHint("Hello")
            fm.executePendingTransactions()
        }

        placeAutocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(AddLocationFragment.TAG, "Place: " + place.name)
            }
            override fun onError(status: Status) {
                Log.i(AddLocationFragment.TAG, "An error occurred: $status")
            }
        })


        return view
    }

    fun onCheckboxClicked(view: View){
        if(view is CheckBox){
            val checked: Boolean = view.isChecked

            when(view.id){
                R.id.checkbox_monday -> {
                    if (checked) {
                        date[monday] = 1
                    }else{
                        date[monday] = 0
                    }
                }
                R.id.checkbox_tuesday -> {
                    if (checked) {
                        date[tuesday] = 1
                    }else{
                        date[tuesday] = 0
                    }
                }
                R.id.checkbox_wednesday -> {
                    if (checked) {
                        date[wednesday] = 1
                    }else{
                        date[wednesday] = 0
                    }
                }
                R.id.checkbox_thursday -> {
                    if (checked) {
                        date[thursday] = 1
                    }else{
                        date[thursday] = 0
                    }
                }
                R.id.checkbox_friday -> {
                    if (checked) {
                        date[friday] = 1
                    }else{
                        date[friday] = 0
                    }
                }
                R.id.checkbox_saturday -> {
                    if (checked) {
                        date[saturday] = 1
                    }else{
                        date[saturday] = 0
                    }
                }
                R.id.checkbox_sunday -> {
                    if (checked) {
                        date[sunday] = 1
                    }else{
                        date[sunday] = 0
                    }
                }
            }
            println(Arrays.toString(date))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFromAndToTime()
        setSeekBar()
        button_next.setOnClickListener{
            Log.d(TAG, "Clicked")
            mFragmentNavigation.pushFragment(Host2Fragment.newInstance(0))
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
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

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
        }
    }
}
