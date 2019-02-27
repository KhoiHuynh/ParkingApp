package com.example.khoi.parkingapp.bean

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val spot = MutableLiveData<SpotClass>()
}