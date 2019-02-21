package com.example.khoi.parkingapp.fragments


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.khoi.parkingapp.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.common.api.Status


class MapFragment : BaseFragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var searchedLocation: LatLng
    private lateinit var  autocompleteFragment: PlaceAutocompleteFragment

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
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
            println("callllled")
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
    private fun setUpMap(){
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

        // location stuff
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isMapToolbarEnabled = false
    }

    override fun onMarkerClick(p0: Marker?) = false

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
