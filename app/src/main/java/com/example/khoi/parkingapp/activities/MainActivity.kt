package com.example.khoi.parkingapp.activities

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.lifecycle.ViewModelProviders
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.bean.SharedViewModel
import com.example.khoi.parkingapp.fragments.*
import com.example.khoi.parkingapp.fragments.MapFragment
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavLogger
import com.ncapdevi.fragnav.FragNavSwitchController
import com.ncapdevi.fragnav.FragNavTransactionOptions
import com.ncapdevi.fragnav.tabhistory.UniqueTabHistoryStrategy

class MainActivity : AppCompatActivity(), BaseFragment.FragmentNavigation, FragNavController.TransactionListener, FragNavController.RootFragmentListener {
    override fun switchTab(i: Int) {
        fragNavController.switchTab(i)
    }

    override fun clearStack() {
        fragNavController.clearStack()
    }

    override fun popFragments(i: Int) {
        fragNavController.popFragments(i)
    }

    override fun replaceFragment(fragment: Fragment) {
        fragNavController.replaceFragment(fragment)
    }

    private val fragNavController: FragNavController = FragNavController(supportFragmentManager, R.id.container)
    lateinit var bottomBar: BottomNavigationView
    private lateinit var model: SharedViewModel
    companion object {
        private const val TAG = "MainActivity"
        const val INDEX_MAP = FragNavController.TAB1
        const val INDEX_HOST = FragNavController.TAB2
        const val INDEX_PROFILE = FragNavController.TAB3
    }

    override fun pushFragment(fragment: Fragment) {
        fragNavController.pushFragment(fragment)
    }

    override val numberOfRootFragments: Int = 3

    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            INDEX_MAP -> return MapFragment.newInstance(0)
            INDEX_HOST -> return AddLocationFragment.newInstance(0)
            INDEX_PROFILE -> return SettingsFragment.newInstance(0)
        }
        throw IllegalStateException("Need to send an index that we know")
    }

    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
        //do fragmentty stuff. Maybe change title, I'm not going to tell you how to live your life
        // If we have a backstack, show the back button
        if(fragment is MapFragment){
            supportActionBar?.hide()
        }
        if(fragment is Host2Fragment){
            supportActionBar?.title = "Register"
        }
        if(fragment is AddLocationFragment){
            supportActionBar?.title = "Host"
        }
        if(fragment is SettingsFragment){
            supportActionBar?.title = "Profile"
        }
        if(fragment is RentFragment){
            supportActionBar?.title = "Rent"
            supportActionBar?.show()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
    }

    @SuppressLint("RestrictedApi")
    override fun onTabTransaction(fragment: Fragment?, index: Int) {
        // If we have a backstack, show the back button
        if(fragment is MapFragment){
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setShowHideAnimationEnabled(false)

            supportActionBar?.hide()
        }
        if(fragment is AddLocationFragment){
            supportActionBar?.title = "Host"
            supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
            supportActionBar?.setShowHideAnimationEnabled(false)

            supportActionBar?.show()
        }
        if(fragment is Host2Fragment){
            supportActionBar?.title = "Register"
            supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
            supportActionBar?.setShowHideAnimationEnabled(false)

            supportActionBar?.show()
        }
        if(fragment is SettingsFragment){
            supportActionBar?.title = "Profile"
            supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
            supportActionBar?.setShowHideAnimationEnabled(false)

            supportActionBar?.show()
        }
        if(fragment is RentFragment){
            supportActionBar?.title = "Rent"
            supportActionBar?.setDisplayHomeAsUpEnabled(fragNavController.isRootFragment.not())
            supportActionBar?.setShowHideAnimationEnabled(false)

            supportActionBar?.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> fragNavController.popFragment()
        }
        return true
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item->
        when (item.itemId) {
            R.id.nav_map -> {
                fragNavController.switchTab(INDEX_MAP)
                if(MapFragment().isVisible){
                    println("i see you")
                }else{
                    println("i see nothing")
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_location -> {
                println("i see: "+fragNavController.currentFrag)
                if(fragNavController.currentFrag!!.isVisible){
                println("i see you")
            }else{
                println("i see nothing")
            }
                fragNavController.switchTab(INDEX_HOST)
                println("i see after: "+fragNavController.currentFrag)

                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_settings -> {
                fragNavController.switchTab(INDEX_PROFILE)
                return@OnNavigationItemSelectedListener true
            }
        }
            false
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model = this.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        }

        supportActionBar?.setShowHideAnimationEnabled(false)
        bottomBar = findViewById(R.id.bottom_navigation)
        fragNavController.apply {
            transactionListener = this@MainActivity
            rootFragmentListener = this@MainActivity
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    Log.e(TAG, message, throwable)
                }
            }
            defaultTransactionOptions = FragNavTransactionOptions.newBuilder().build()
            fragmentHideStrategy = FragNavController.HIDE
            navigationStrategy = UniqueTabHistoryStrategy(object : FragNavSwitchController {
                override fun switchTab(index: Int, transactionOptions: FragNavTransactionOptions?) {
                    if(index == INDEX_MAP){
                        bottomBar.selectedItemId = R.id.nav_map
                    }
                    if(index == INDEX_HOST){
                        bottomBar.selectedItemId = R.id.nav_add_location
                    }
                    if(index == INDEX_PROFILE){
                        bottomBar.selectedItemId = R.id.nav_settings
                    }
                }
            })
        }

        fragNavController.initialize(INDEX_MAP, savedInstanceState)
        bottomBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//        bottomBar.setOnNavigationItemReselectedListener { fragNavController.clearStack() }
    }

    override fun onBackPressed() {
        if (fragNavController.popFragment().not()) {
            super.onBackPressed()
        }
//        println("ccurent frag: " + fragNavController.currentFrag)
//        if (fragNavController.currentFrag is AddLocationFragment){
//            println("True")
//            spotObj.setDays(intArrayOf(0,0,0,0,0,0,0))
//            spotObj.setPlace(null)
//            model.spot.postValue(null)
//        }
    }

//    override fun onSaveInstanceState(outState: Bundle?) {
//        if (outState != null) {
//            super.onSaveInstanceState(outState)
//        }
//        fragNavController.onSaveInstanceState(outState!!)
//    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        fragNavController.onSaveInstanceState(outState!!)
    }

}
