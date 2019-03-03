package com.example.khoi.parkingapp.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.bean.SharedViewModel
import com.example.khoi.parkingapp.bean.Spot
import com.example.khoi.parkingapp.bean.SuccessDialog
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_host2.*

class Host2Fragment : BaseFragment(){
    private var spotObj = Spot()
    private lateinit var model: SharedViewModel
    lateinit var bottomBar: BottomNavigationView
    lateinit var searchedLocation: LatLng
    private lateinit var mContext: FragmentActivity
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
        mContext = activity as FragmentActivity
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
                spotObj = it
                textView_address.text = spotObj.getPlace()?.name.toString()
                searchedLocation = LatLng(spotObj.getPlace()!!.latLng.latitude, spotObj.getPlace()!!.latLng.longitude)
//                latLng = it.getPlace()
//                textView.text = it.getRate().toString()
//                println("host2" + it.getPlace())
//                println("host2" + Arrays.toString(it.getDates()))
//                println("host2" + it.getRate())
//                println("host2" + it.getTimeFrom())
//                println("host2" + it.getTimeTo())
            }
        })
        val button = view.findViewById(R.id.button_register) as Button
        val dialog = SuccessDialog()

        button.setOnClickListener{
            Log.d(TAG, "Clicked")
            val description = edit_text_description.text.toString()

            println("description: " + description)
            dialog.show(fragmentManager, "success dialog")
            spotObj.setDescription(description)
            Log.d(TAG, "spotObj description" + spotObj.getDescription())
            saveUserToDatabase()
            mFragmentNavigation.clearStack()
            mFragmentNavigation.switchTab(0)
            bottomBar.selectedItemId = R.id.nav_map

            model.spotLatLng.postValue(searchedLocation)
            model.spot.postValue(spotObj)
            spotObj.printMe()

//            Log.d(TAG, "Full Spot Object: \n" + spotObj.printMe())

//            val mapFragment = MapFragment()
//            mapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(searchedLocation, 12f))

//            MapFragment().setUpMap(searchedLocation)

        }

    }

    private fun saveUserToDatabase(){
        val query = FirebaseDatabase.getInstance().getReference("spots/")
            .orderByChild("place/id").equalTo(spotObj.getPlace()?.id)

        query.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d(TAG, "Spot already exists")
                    Toast.makeText(mContext, "This spot already exists", Toast.LENGTH_SHORT).show()
                    for (issue:DataSnapshot in dataSnapshot.children) {
                        println("id value: " + issue.child("place/id").value)
                    }

                }
                else{
                    val uid = FirebaseAuth.getInstance().uid.toString()
                    val key = FirebaseDatabase.getInstance().getReference("spots").push().key
                    val ref = FirebaseDatabase.getInstance().getReference("/spots/$key")

                    spotObj.setUid(uid)
                    ref.setValue(spotObj)
                        .addOnSuccessListener {
                            Log.d(TAG, "Added spot $key to the Database success")
                        }
                        .addOnFailureListener{
                            Log.w(TAG, "saveUserToDatabase: Failed", it)
                        }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}
