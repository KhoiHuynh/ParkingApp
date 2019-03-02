package com.example.khoi.parkingapp.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.bean.SharedViewModel
import com.example.khoi.parkingapp.bean.SuccessDialog
import com.google.android.gms.location.places.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_host2.*

class Host2Fragment : BaseFragment(){
    private lateinit var model: SharedViewModel
    lateinit var bottomBar: BottomNavigationView
    lateinit var searchedLocation: LatLng
    companion object {
        private const val TAG = "Host2Fragment"
        fun newInstance(instance: Int): Host2Fragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = Host2Fragment()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_host2, container, false)
        bottomBar = activity?.findViewById(R.id.bottom_navigation)!!
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
//                searchedLocation = LatLng(place.latLng.latitude,place.latLng.longitude)

        model.spot.observe(this, Observer {it ->
            it?.let {
                textView_address.text = it.getAddress()?.name.toString()
                searchedLocation = LatLng(it.getAddress()!!.latLng.latitude, it.getAddress()!!.latLng.longitude)

//                latLng = it.getAddress()
//                textView.text = it.getRate().toString()
//                println("host2" + it.getAddress())
//                println("host2" + Arrays.toString(it.getDates()))
//                println("host2" + it.getRate())
//                println("host2" + it.getTimeFrom())
//                println("host2" + it.getTimeTo())
            }
        })
        val description = edit_text_description.text.toString()
        val button = view.findViewById(R.id.button_register) as Button
        button.setOnClickListener{
            Log.d(TAG, "Clicked")
            println("description: " + description)
            val dialog = SuccessDialog()
            dialog.show(fragmentManager, "success dialog")
            spotObj.setDescription(description)
            mFragmentNavigation.clearStack()
            bottomBar.selectedItemId = R.id.nav_map
            mFragmentNavigation.switchTab(0)
            model.spotLatLng.postValue(searchedLocation)
            model.spot.postValue(spotObj)

//            val mapFragment = MapFragment()
//            mapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 12f))

//            MapFragment().setUpMap(searchedLocation)

        }

    }

}
