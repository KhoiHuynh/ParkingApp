package com.example.khoi.parkingapp.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.bean.SharedViewModel
import kotlinx.android.synthetic.main.fragment_host2.*
import java.util.*

class Host2Fragment : BaseFragment(){
    private lateinit var model: SharedViewModel

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = activity?.run {
            ViewModelProviders.of(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        model.spot.observe(this, Observer {it ->
            it?.let {
                textView_address.text = it.getAddress()
//                textView.text = it.getRate().toString()
//                println("host2" + it.getAddress())
//                println("host2" + Arrays.toString(it.getDates()))
//                println("host2" + it.getRate())
//                println("host2" + it.getTimeFrom())
//                println("host2" + it.getTimeTo())
            }
        })


        val button = view.findViewById(R.id.button_register) as Button
        button.setOnClickListener{
            Log.d(TAG, "Clicked")
        }

    }
}
