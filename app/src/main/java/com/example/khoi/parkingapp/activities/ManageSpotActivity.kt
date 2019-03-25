package com.example.khoi.parkingapp.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.example.khoi.parkingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.graphics.drawable.ColorDrawable
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenu
import com.baoyz.swipemenulistview.SwipeMenuCreator
import android.graphics.Color
import android.os.Build
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.collections.HashMap
import android.widget.*
import com.example.khoi.parkingapp.fragments.markerMap
import com.example.khoi.parkingapp.fragments.markerMap2
import com.google.android.gms.maps.model.Marker


class ManageSpotActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ManageSpotActivity"
    }

    private var listOfMaps: ArrayList<HashMap<String?, DataSnapshot>> = ArrayList()
    private var listSpotIds: ArrayList<String> = ArrayList()
    private var spotList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_spot)
        setUpList()
    }

    private fun setUpList(){
        val listView: SwipeMenuListView = findViewById(R.id.listView)
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        Log.d(TAG, "currentUser: $currentUser")

        val database = FirebaseDatabase.getInstance()
        val query = database.getReference("spots").orderByChild("uid").equalTo(currentUser)

        query.addListenerForSingleValueEvent(object : ValueEventListener{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d(TAG, "querying")
                    for(spot:DataSnapshot in dataSnapshot.children){
                        val spotMap: HashMap<String?, DataSnapshot> = HashMap() //key and address
                        val spotId = spot.key
                        val spotAddress = spot.child("place/address").value.toString()
                        spotMap.put(spotId, spot)
                        if (spotId != null) {
                            listSpotIds.add(spotId)
                        }
                        listOfMaps.add(spotMap)
                        spotList.add(spotAddress)

                    }
                    createSwipeMenuList(spotList, listView)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Failed to query for spot")
            }
        })
    }

    private fun createSwipeMenuList(spotList: ArrayList<String>, listView: SwipeMenuListView){
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, spotList)
        listView.adapter = adapter

        val creator = SwipeMenuCreator { menu ->
            // create "open" item
            Log.d(TAG, "CREATING")
            val openItem = SwipeMenuItem(applicationContext)
            // set item background
            openItem.background = ColorDrawable(Color.rgb(66, 244, 78))
            // set item width
            openItem.width = 200
            // set item title
            openItem.title = "Active"
            // set item title fontsize
            openItem.titleSize = 12
            // set item title font color
            openItem.titleColor = Color.BLACK
            openItem.setIcon(R.drawable.ic_location_on_black_24dp)
            // add to menu
            menu.addMenuItem(openItem)

            // create "delete" item
            val deleteItem = SwipeMenuItem(applicationContext)
            // set item background
            deleteItem.background = ColorDrawable(Color.rgb(0xF9, 0x3F,0x25))
            // set item width
            deleteItem.width = 200
            deleteItem.title = "Delete"
            deleteItem.titleSize = 12
            deleteItem.titleColor = Color.BLACK

            // set a icon
            deleteItem.setIcon(R.drawable.ic_delete)
            // add to menu
            menu.addMenuItem(deleteItem)
        }

        // set creator
        listView.setMenuCreator(creator)

        listView.setOnMenuItemClickListener(object : SwipeMenuListView.OnMenuItemClickListener{
            override fun onMenuItemClick(position: Int, menu: SwipeMenu?, index: Int): Boolean {
                when (index) {
                    0 -> { //active and inactive
                        openDialog(position, listView)

                    }
                    1 -> { //delete
                        deleteLocation(position, listView, menu, adapter)
                    }
                }
                // false : close the menu; true : not close the menu
                return false
            }
        })
    }

    private fun openDialog(position: Int, listView: SwipeMenuListView) {
        val dialog = Dialog(this)
        var flag = false
        dialog.setContentView(R.layout.dialog_spot_management)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams().apply {
            copyFrom(dialog.window?.attributes)
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
        }

        val switch = dialog.findViewById<View>(R.id.switch_activate_deactivate) as Switch
        val cancelButton = dialog.findViewById<View>(R.id.button_cancel) as Button
        val okButton = dialog.findViewById<View>(R.id.button_ok) as Button

        // setup the switch positions
        val tempMap: HashMap<String?, DataSnapshot> = listOfMaps[position]
        val snap = tempMap.get(listSpotIds[position]) //this is a data snapshot of the spot we clicked on in the list
        val spotId = snap?.key
        val query = FirebaseDatabase.getInstance().getReference("spots").orderByKey().equalTo(spotId)

        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for (spot: DataSnapshot in p0.children) {
                        val availability = spot.child("availability").value.toString()
                        if (availability.toLowerCase() != "disabled"){
                            switch.isChecked = true
                        }
                        if(availability.toLowerCase() == "disabled"){
                            switch.isChecked = false
                        }
                    }

                }

            }
            override fun onCancelled(p0: DatabaseError) {
            }

        })

        switch.setOnClickListener {
            flag = true
        }

        okButton.setOnClickListener{
            if(flag) { // the switch was changed
                if (switch.isChecked) { // then we want to activate the spot (deactivated --> activated)
                    val ref = FirebaseDatabase.getInstance().getReference("spots/$spotId/availability")
                    ref.setValue("available")
                    val markerToUpdate = markerMap2.get(spotId)
                    markerToUpdate?.isVisible = true
                    flag = false
                    dialog.dismiss()
                    Toast.makeText(this@ManageSpotActivity, "Your spot is reactivated", Toast.LENGTH_LONG).show()
                }
                else { //this is when the user disables a spot
                    val ref = FirebaseDatabase.getInstance().getReference("spots/$spotId/availability")
                    ref.setValue("disabled")
                    val markerToUpdate = markerMap2.get(spotId)
                    markerToUpdate?.isVisible = false
                    flag = false
                    dialog.dismiss()
                    Toast.makeText(this@ManageSpotActivity, "Your spot is deactivated", Toast.LENGTH_LONG).show()
                }
            }
            else{ // nothing was changed, we just dismiss the dialog
                dialog.dismiss()
            }
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.attributes = lp

    }

    private fun deleteLocation(position: Int, listView: SwipeMenuListView, menu: SwipeMenu?, adapter: ArrayAdapter<String>){
        var key: String? = null
        val tempMap: HashMap<String?, DataSnapshot> = listOfMaps[position]
        val snap = tempMap.get(listSpotIds[position]) //this is a data snapshot of the spot we clicked on in the list
        val spotId = snap?.key

        val query = FirebaseDatabase.getInstance().getReference("spots").orderByKey().equalTo(spotId)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "DatabaseError: " + p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(spot: DataSnapshot in p0.children){
                        key = spot.key
                    }

                    val tempMarkerMap: HashMap<String, Marker> = markerMap2
                    val markerToDelete: Marker = tempMarkerMap.get(key)!!

                    FirebaseDatabase.getInstance().getReference("spots/$spotId").removeValue().addOnSuccessListener {
                        spotList.removeAt(position)
                        listOfMaps.removeAt(position)
                        listSpotIds.removeAt(position)
                        markerMap2.remove(key)
                        markerMap.remove(markerToDelete.id)
                        markerToDelete.remove()
                        Toast.makeText(this@ManageSpotActivity, "Your spot has been deleted", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Spot has been removed and deleted from DB")
                        listView.adapter = adapter
                    }
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->{
                onBackPressed()
            }
        }
        return true
    }
}