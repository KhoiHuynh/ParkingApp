package com.example.khoi.parkingapp

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import android.content.res.Resources
import android.location.Location
import android.support.v4.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var searchedLocation: LatLng


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val TAG = "MapsActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // The main entry point for interacting with the fused location provider.
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        getAutoCompleteSearchResults()
    }

    private fun getAutoCompleteSearchResults() {
        if (fragmentManager != null) {
            val autocompleteFragment =
                fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    // TODO: Get info about the selected place.
                    Log.i(MapsActivity.TAG, "Place: " + place.name)
                    searchedLocation = LatLng(place.latLng.latitude,place.latLng.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 12f))
                }

                override fun onError(status: Status) {
                    Log.i(MapsActivity.TAG, "An error occurred: $status")
                }
            })
        }
    }

    private fun showAndHideFragment(itemId: Int){
        val fragmentManager = this@MapsActivity.supportFragmentManager
        val addLocationFragment: Fragment? = fragmentManager.findFragmentByTag("addLocationFragment")
        val settingsFragment   : Fragment? = fragmentManager.findFragmentByTag("settingsFragment")

        Log.d(MapsActivity.TAG, "# of Fragments: ${fragmentManager.fragments.size}")
        if(itemId == R.id.nav_map){
            if(settingsFragment != null && settingsFragment.isVisible){
                fragmentManager.beginTransaction().hide(settingsFragment).commit()
            }
            if(addLocationFragment != null && addLocationFragment.isVisible){
                fragmentManager.beginTransaction().hide(addLocationFragment).commit()
            }
        }
        if(itemId == R.id.nav_add_location){
            if(addLocationFragment == null){
                fragmentManager.beginTransaction().add(R.id.fragmentContainer, AddLocationFragment(),"addLocationFragment").commit()
            }else{
                fragmentManager.beginTransaction().show(addLocationFragment).commit()
            }
            if(settingsFragment != null && settingsFragment.isVisible){
                fragmentManager.beginTransaction().hide(settingsFragment).commit()
            }
        }
        if(itemId == R.id.nav_settings){
            if(settingsFragment == null){
                fragmentManager.beginTransaction().add(R.id.fragmentContainer, SettingsFragment(),"settingsFragment").commit()
            }else{
                fragmentManager.beginTransaction().show(settingsFragment).commit()
            }
            if(addLocationFragment != null && addLocationFragment.isVisible){
                fragmentManager.beginTransaction().hide(addLocationFragment).commit()
            }
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item->
        when(item.itemId){
            R.id.nav_map -> {
                Log.d(MapsActivity.TAG, "map pressed")
                showAndHideFragment(R.id.nav_map)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_location -> {
                Log.d(MapsActivity.TAG, "add location pressed")
                showAndHideFragment(R.id.nav_add_location)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                Log.d(MapsActivity.TAG, "settings pressed")
                showAndHideFragment(R.id.nav_settings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        checkPermission()
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_json
                )
            )

            if (!success) {
                Log.e(MapsActivity.TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(MapsActivity.TAG, "Can't find style. Error: ", e)
        }

        // location stuff
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.uiSettings.isMapToolbarEnabled = false


    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        setUpMap()
    }

    private fun setUpMap(){
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }else{
                Log.d(MapsActivity.TAG, "Last Location is Null")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the location-related task you need to do.
                    Log.d(MapsActivity.TAG, "Location permission granted")
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
