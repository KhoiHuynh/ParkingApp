package com.example.khoi.parkingapp

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
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val TAG = "MapsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Log.d("MapsActivity", "in the maps")

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val fragmentManager= this@MapsActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        Log.d(TAG, "This is our fragments: " + fragments.toString())

        getAutoCompleteSearchResults()
    }

    override fun onStart() {
        super.onStart()
        val fragmentManager= this@MapsActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        Log.d(TAG, "onStart: This is our fragments: " + fragments.toString())
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    private fun getAutoCompleteSearchResults(){
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
// TODO fix deprecated fragmentManager getter
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.name)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item->
        when(item.itemId){
            R.id.nav_map -> {
                Log.d(TAG, "map pressed")
                // if there's a fragment, close it
                val visibleFrag = getVisibleFragment()
                if(visibleFrag != null){
                    detachFragment(visibleFrag)
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_location -> {
                Log.d(TAG, "add location pressed")
                replaceFragment(AddLocationFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                Log.d(TAG, "settings pressed")
                replaceFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    private fun getVisibleFragment(): Fragment? {
        val fragmentManager= this@MapsActivity.supportFragmentManager
        val fragments = fragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments!!) {
                if (fragment != null && fragment!!.isVisible)
                    Log.d(TAG, "found a visible fragment: ")
                    return fragment
            }
        }
        Log.d(TAG, "No visible fragment: " + fragments.isEmpty())
        return null
    }

    private fun detachFragment(fragment: Fragment){
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction.remove(fragment)
        fragmentTransaction.commit()
    }







    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
