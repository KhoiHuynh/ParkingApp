package com.example.khoi.parkingapp.bean

import android.util.Log
import com.google.android.gms.location.places.Place
import java.util.*
class Spot{
    companion object {
        private val TAG = Spot::class.simpleName
    }
    private var uid: String? = null
    private var place: Place? = null
    private var dates : IntArray? = null
    private var timeFrom: String? = null
    private var timeTo: String? = null
    private var rate: String? = null
    private var description: String? = null

    //getters
    fun getUid() : String? {return uid}
    fun getPlace() : Place? {return place}
    fun getDates() : IntArray? {return this.dates}
    fun getTimeFrom() : String? {return timeFrom}
    fun getTimeTo() : String? {return timeTo}
    fun getRate() : String? {return rate}
    fun getDescription() : String? {return description}


    //setters
    fun setUid(uid: String?){this.uid = uid}
    fun setPlace(place: Place?){this.place = place}
    fun setDates(dates: IntArray){
        println("incoming setter param: " + Arrays.toString(dates))
        this.dates = dates
        println("setter: " + Arrays.toString(this.dates))
    }
    fun setTimeFrom(timeFrom: String){this.timeFrom = timeFrom}
    fun setTimeTo(timeTo: String){this.timeTo = timeTo}
    fun setRate(rate: String){this.rate = rate}
    fun setDescription(description: String){this.description = description}


    fun printMe(){
        Log.d(TAG,("\nThis is User: $uid " +
                "\nThis is place: $place " +
                "\nThis is dates: ${Arrays.toString(getDates())}" +
                "\nThis is timeFrom: $timeFrom" +
                "\nThis is timeTo: $timeTo" +
                "\nThis is rate: $rate" +
                "\nThis is description: $description"))
    }
}
