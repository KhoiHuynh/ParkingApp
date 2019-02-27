package com.example.khoi.parkingapp.bean

class SpotClass{
    private var address: String? = null
    private var dates: Array<Int>? = null
    private var timeFrom: String? = null
    private var timeTo: String? = null
    private var rate: String? = null
    private var description: String? = null

    //getters
    fun getAddress() : String? {return address}
    fun getDates() : Array<Int>? {return dates}
    fun getTimeFrom() : String? {return timeFrom}
    fun getTimeTo() : String? {return timeTo}
    fun getRate() : String? {return rate}
    fun getDescription() : String? {return description}


    //setters
    fun setAddress(address: String){this.address = address}
    fun setDates(dates: Array<Int>){this.dates = dates}
    fun setTimeFrom(timeFrom: String){this.timeFrom = timeFrom}
    fun setTimeTo(timeTo: String){this.timeTo = timeTo}
    fun setRate(rate: String){this.rate = rate}
    fun setDescription(description: String){this.description = description}


    fun printMe(){
        println("This is address: $address " +
                "\nThis is dates: $dates" +
                "\nThis is timeFrom: $timeFrom" +
                "\nThis is timeTo: $timeTo" +
                "\nThis is rate: $rate" +
                "\nThis is description: $description")
    }
}
