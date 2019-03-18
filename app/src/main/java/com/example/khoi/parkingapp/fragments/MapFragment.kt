package com.example.khoi.parkingapp.fragments

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import androidx.core.app.ActivityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.bean.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

var markerMap: HashMap<String, DataSnapshot> = HashMap()
var markerMap2: HashMap<String, Marker> = HashMap()
var addTrigger: Boolean = false
class MapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    lateinit var mMap: GoogleMap
    private lateinit var model: SharedViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var searchedLocation: LatLng
    private lateinit var  autocompleteFragment: PlaceAutocompleteFragment
    private lateinit var database: FirebaseDatabase
    private lateinit var row: View
    private var addedNewSpot: Boolean = false
    private lateinit var bitmapdraw: BitmapDrawable
    private lateinit var b: Bitmap
    private lateinit var customMarker: Bitmap
    private lateinit var bitmapdraw2: BitmapDrawable
    private lateinit var b2: Bitmap
    private lateinit var customMarker2: Bitmap


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "MapFragment"
        fun newInstance(instance: Int): MapFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = MapFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        model = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bitmapdraw = ContextCompat.getDrawable(context!!,R.drawable.marker_logo) as BitmapDrawable
        b = bitmapdraw.bitmap
        customMarker = Bitmap.createScaledBitmap(b,65, 92, false)

        bitmapdraw2 = ContextCompat.getDrawable(context!!,R.drawable.ic_grey_marker) as BitmapDrawable
        b2 = bitmapdraw2.bitmap
        customMarker2 = Bitmap.createScaledBitmap(b2,65, 92, false)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        row = layoutInflater.inflate(R.layout.custom_info_window, null)
        database = FirebaseDatabase.getInstance()
        Handler().postDelayed({
            loadMarkersFromDB(null, null)
        }, 100)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        getAutoCompleteSearchResults()
        val fm = childFragmentManager
        var mapFragment: SupportMapFragment? = fm.findFragmentByTag("mapFragmentContainer") as SupportMapFragment?
        if (mapFragment == null) {
            mapFragment = SupportMapFragment()
            val ft = fm.beginTransaction()
            ft.add(R.id.mapFragmentContainer, mapFragment, "mapFragmentContainer")
            ft.commit()
            fm.executePendingTransactions()
        }
        mapFragment.getMapAsync(this)
        return view
    }

    private fun getAutoCompleteSearchResults() {
        if (activity?.fragmentManager != null) {
            autocompleteFragment =
                activity?.fragmentManager?.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.name)
                    searchedLocation = LatLng(place.latLng.latitude,place.latLng.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 12f))
                }

                override fun onError(status: Status) {
                    Log.i(TAG, "An error occurred: $status")
                }
            })
        }
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(activity!!.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity!!,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        setUpMap()
    }

    @SuppressLint("MissingPermission")
    fun setUpMap(){
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(activity!!) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }else{
                Log.d(TAG, "Last Location is Null")
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkPermission()

        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    activity, R.raw.style_json
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

// adding new marker to map
        model.spot.observe(this, Observer { spot ->
            spot?.let {
                if(addTrigger){
                    mFragmentNavigation.clearStack()
                    val strTime = it.getTimeFrom() + " - " + it.getTimeTo()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.getPlace()?.latLng, 12f))
                    val marker = mMap.addMarker(MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(customMarker))
                        .position(it.getPlace()?.latLng!!)
                        .title(it.getPlace()?.address.toString())
                        .snippet(strTime + "\n" + it.getRate() + "0 $/h"))

                    val id = it.getPlace()!!.id
                    loadMarkersFromDB(marker, id)
                    addTrigger = false
//                    val query = database.getReference("spots/").orderByChild("place/id").equalTo(id)
//                    query.addListenerForSingleValueEvent(object: ValueEventListener{
//                        override fun onDataChange(dataSnapshot: DataSnapshot) {
//                            if(dataSnapshot.exists()){
//                                markerMap.put(marker, dataSnapshot)
//                                Log.d(TAG, "Adding marker '${it.getPlace()?.name.toString()} at position ${it.getPlace()?.latLng!!}")
//                                addTrigger = false
//                            }
//                        }
//                        override fun onCancelled(p0: DatabaseError) {
//                        }
//                    })
                }

            }
        })

        // location stuff
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isMapToolbarEnabled = false

        //setting up the info window for each marker
        mMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker?): View {
                marker?.setInfoWindowAnchor(0.5f,-0.15f)
                val info: View = layoutInflater.inflate(R.layout.custom_info_window, null)
                val tvAddress: TextView = info.findViewById(R.id.textView_iw_address)
                val tvTime: TextView = info.findViewById(R.id.textView_iw_time)

                tvAddress.text = marker?.title
                tvTime.text = marker?.snippet

                return info
            }

            override fun getInfoContents(marker: Marker?): View? {
                return null
            }
        })

        //onClick of the info Window
        mMap.setOnInfoWindowClickListener {marker ->
            if(marker.tag == "rented"){
                Toast.makeText(activity, "This spot is currently occupied", Toast.LENGTH_LONG).show()
            }
            else{
                val bundle = Bundle()
                val spot: DataSnapshot = markerMap.get(marker.id)!!

                val t = object : GenericTypeIndicator<ArrayList<Int>>(){}
                val address = spot.child("place/address/").value.toString()
                val description = spot.child("description/").value.toString()
                val rate = spot.child("rate/").value.toString()
                val days: ArrayList<Int> = spot.child("days/").getValue(t)!!
                val fromTime = spot.child("timeFrom/").value.toString()
                val toTime = spot.child("timeTo/").value.toString()
                val spotId = spot.key

                bundle.putString("address", address)
                bundle.putString("description", description)
                bundle.putString("rate", rate)
                bundle.putIntegerArrayList("days", days)
                bundle.putString("fromTime", fromTime)
                bundle.putString("toTime", toTime)
                bundle.putString("spotId", spotId)

                val rentFragment: Fragment = RentFragment.newInstance(0)
                rentFragment.arguments = bundle
                mFragmentNavigation.pushFragment(rentFragment)
            }
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun loadMarkersFromDB(newMarker: Marker?, placeId: String?){
        if(newMarker != null && placeId != null){
            val query = database.getReference("spots/").orderByChild("place/id").equalTo(placeId)
            query.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        for(newSpot:DataSnapshot in dataSnapshot.children){
                            val lat: Double = newSpot.child("place/latLng/latitude/").value.toString().toDouble()
                            val lng: Double = newSpot.child("place/latLng/longitude/").value.toString().toDouble()
                            val position = LatLng(lat, lng)
                            markerMap.put(newMarker.id, newSpot)
                            markerMap2.put(newSpot.key!!, newMarker)
                            Log.d(TAG, "Loading new Marker at position: $position")
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }
        if(newMarker == null && placeId == null){
            val query = database.getReference("spots/").orderByChild("place/latLng")
            query.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()){
                        var lat: Double
                        var lng: Double
                        var position: LatLng
                        var availability: String
                        for(spot:DataSnapshot in dataSnapshot.children){
                            var marker: Marker
                            availability = spot.child("availability").value.toString()

                            Log.d(TAG, "available: $availability")

                            lat = spot.child("place/latLng/latitude/").value.toString().toDouble()
                            lng = spot.child("place/latLng/longitude/").value.toString().toDouble()
                            position = LatLng(lat, lng)
                            val strTime = spot.child("timeFrom").value.toString() + " - " +
                                    spot.child("timeTo").value.toString()
                            if(availability == "rented"){ //load with the grey marker
                                Log.d(TAG, "in here")

                                marker = mMap.addMarker(MarkerOptions()
                                    .position(position)
                                    .icon(BitmapDescriptorFactory.fromBitmap(customMarker2))
                                    .title(spot.child("place/address").value.toString())
                                    .snippet(strTime + "\n" + spot.child("rate").value.toString() + "0 $/h"))
                                marker.tag = "rented"
                            }
                            else{ // load with the normal marker
                                Log.d(TAG, "no here")

                                marker = mMap.addMarker(MarkerOptions()
                                    .position(position)
                                    .icon(BitmapDescriptorFactory.fromBitmap(customMarker))
                                    .title(spot.child("place/address").value.toString())
                                    .snippet(strTime + "\n" + spot.child("rate").value.toString() + "0 $/h"))
                            }


                            val key = spot.key
//                            val ref = database.getReference("spots/$key/marker_id")
//                            ref.setValue(marker.id)

                            markerMap.put(marker.id, spot)
                            if (key != null) {
                                markerMap2.put(key,marker)
                            }
                            Log.d(TAG,"MARKER ID:" + spot.key)
                            Log.d(TAG, "Loading markers at position: $position")


                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            } )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    Log.d(TAG, "Location permission granted")
                    setUpMap()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
