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
import android.content.res.Resources
import com.google.android.gms.maps.model.MapStyleOptions



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

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        getAutoCompleteSearchResults()
    }

    private fun getAutoCompleteSearchResults(){
        val autocompleteFragment =
            fragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment
// TODO fix deprecated fragmentManager getter
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.name)
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }
        })
    }

    private fun showAndHideFragment(itemId: Int){
        val fragmentManager = this@MapsActivity.supportFragmentManager
        val addLocationFragment: Fragment? = fragmentManager.findFragmentByTag("addLocationFragment")
        val settingsFragment   : Fragment? = fragmentManager.findFragmentByTag("settingsFragment")

        Log.d(TAG, "# of Fragments: ${fragmentManager.fragments.size}")
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
        val fragmentManager= this@MapsActivity.supportFragmentManager
        val fragments = fragmentManager.fragments

        when(item.itemId){
            R.id.nav_map -> {
                Log.d(TAG, "map pressed")
                showAndHideFragment(R.id.nav_map)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_location -> {
                Log.d(TAG, "add location pressed")
                showAndHideFragment(R.id.nav_add_location)
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                Log.d(TAG, "settings pressed")
                showAndHideFragment(R.id.nav_settings)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_json
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}
