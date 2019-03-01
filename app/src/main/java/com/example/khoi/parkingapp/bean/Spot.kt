package com.example.khoi.parkingapp.bean

import com.google.android.gms.location.places.Place
import java.math.BigDecimal
import java.util.*
class Spot{
    private var address: Place? = null
    private var dates : IntArray? = null
    private var timeFrom: String? = null
    private var timeTo: String? = null
    private var rate = BigDecimal(0.0)
    private var description: String? = null

    //getters
    fun getAddress() : Place? {return address}
    fun getDates() : IntArray? {return this.dates}
    fun getTimeFrom() : String? {return timeFrom}
    fun getTimeTo() : String? {return timeTo}
    fun getRate() : BigDecimal? {return rate}
    fun getDescription() : String? {return description}


    //setters
    fun setAddress(address: Place?){this.address = address}
    fun setDates(dates: IntArray){
        println("incoming setter param: " + Arrays.toString(dates))
        this.dates = dates
        println("setter: " + Arrays.toString(this.dates))
    }
    fun setTimeFrom(timeFrom: String){this.timeFrom = timeFrom}
    fun setTimeTo(timeTo: String){this.timeTo = timeTo}
    fun setRate(rate: BigDecimal){this.rate = rate}
    fun setDescription(description: String){this.description = description}


    fun printMe(){
        println("This is address: $address " +
                "\nThis is dates: ${Arrays.toString(getDates())}" +
                "\nThis is timeFrom: $timeFrom" +
                "\nThis is timeTo: $timeTo" +
                "\nThis is rate: $rate" +
                "\nThis is description: $description")
    }
}
