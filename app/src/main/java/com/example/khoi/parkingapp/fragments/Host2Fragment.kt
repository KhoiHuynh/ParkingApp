package com.example.khoi.parkingapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.khoi.parkingapp.R

class Host2Fragment : BaseFragment(){
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
        val button = view.findViewById(R.id.button_host2) as Button
        button.setOnClickListener{
            Log.d(TAG, "Clicked")
        }
        return view
    }
}
