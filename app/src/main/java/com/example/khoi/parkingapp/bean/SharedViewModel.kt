package com.example.khoi.parkingapp.bean

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot

class SharedViewModel : ViewModel() {
    val spot = MutableLiveData<Spot>()
    val spotLatLng = MutableLiveData<LatLng>()
}