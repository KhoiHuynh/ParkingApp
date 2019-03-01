package com.example.khoi.parkingapp.bean

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SharedViewModel : ViewModel() {
    val spot = MutableLiveData<Spot>()
    val spotLatLng = MutableLiveData<LatLng>()
}