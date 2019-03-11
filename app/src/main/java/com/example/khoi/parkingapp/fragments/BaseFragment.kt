package com.example.khoi.parkingapp.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    lateinit var mFragmentNavigation: FragmentNavigation
    private var mInt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            mInt = args.getInt(ARGS_INSTANCE)
        }
    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        if (context is FragmentNavigation) {
//            mFragmentNavigation = context
//        }
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentNavigation) {
            mFragmentNavigation = context
        }
    }

    interface FragmentNavigation {
        fun pushFragment(fragment: Fragment)
        fun replaceFragment(fragment: Fragment)
        fun popFragments(i: Int)
        fun clearStack()
        fun switchTab(i: Int)
    }


    companion object {
        const val ARGS_INSTANCE = "com.ncapdevi.sample.argsInstance"
    }
}
