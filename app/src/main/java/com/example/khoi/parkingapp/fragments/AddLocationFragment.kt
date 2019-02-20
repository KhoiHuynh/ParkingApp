package com.example.khoi.parkingapp.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.khoi.parkingapp.R

class AddLocationFragment : BaseFragment() {
    companion object {
        private const val TAG = "AddLocationFragment"
        fun newInstance(instance: Int): AddLocationFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = AddLocationFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById(R.id.button) as Button
        button.setOnClickListener{
            Log.d(TAG, "Clicked")
            mFragmentNavigation.pushFragment(Host2Fragment.newInstance(0))
        }
    }


}
