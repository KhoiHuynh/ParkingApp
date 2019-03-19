package com.example.khoi.parkingapp.bean

import android.util.Log
import com.google.android.gms.location.places.Place
import java.util.*
import kotlin.collections.ArrayList

class Spot{
    companion object {
        private val TAG = Spot::class.simpleName
    }
    private var uid: String? = null
    private var place: Place? = null
    private var days : ArrayList<Int>? = null
    private var timeFrom: String? = null
    private var timeTo: String? = null
    private var rate: String? = null
    private var description: String? = null
    private var key: String? = null
    private var availability: String = "Available"

    //getters
    fun getUid() : String? {return uid}
    fun getPlace() : Place? {return place}
    fun getDays() : ArrayList<Int>? {return this.days}
    fun getTimeFrom() : String? {return timeFrom}
    fun getTimeTo() : String? {return timeTo}
    fun getRate() : String? {return rate}
    fun getDescription() : String? {return description}
    fun getKey() : String? {return key}
    fun getAvailability() : String? { return availability}


    //setters
    fun setUid(uid: String?){this.uid = uid}
    fun setPlace(place: Place?){this.place = place}
    fun setDays(dates: ArrayList<Int>){
        println("incoming setter param: " + dates.forEach { println(it) })
        this.days = dates
        println("setter: " + (this.days)?.forEach { println(it) })
    }
    fun setTimeFrom(timeFrom: String?){this.timeFrom = timeFrom}
    fun setTimeTo(timeTo: String?){this.timeTo = timeTo}
    fun setRate(rate: String){this.rate = rate}
    fun setDescription(description: String){this.description = description}
    fun setKey(key: String?) {this.key = key}
    fun setAvailability(availability: String) { this.availability = availability}


    fun printMe(){
        Log.d(TAG,("\nThis is User: $uid " +
                "\nThis is place: $place " +
                "\nThis is days: ${getDays()?.forEach { println(it) }})}" +
                "\nThis is timeFrom: $timeFrom" +
                "\nThis is timeTo: $timeTo" +
                "\nThis is rate: $rate" +
                "\nThis is description: $description"))
    }


}
