package com.example.khoi.parkingapp.fragments


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.preference.*
import com.example.khoi.parkingapp.R
import com.example.khoi.parkingapp.activities.ManageSpotActivity
import com.example.khoi.parkingapp.activities.RegisterActivity
import com.example.khoi.parkingapp.activities.RentActivity
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.d(TAG, "onCreatePreferences")
        addPreferencesFromResource(R.xml.app_preferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val key2 = sharedPreferences.getString("key2", "YESSSS")
        sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            when(key){
                "key1" -> {
                    println("hello: " + sharedPreferences.getBoolean(key, false))
                }
                "key2" -> {
                    println("hello: " + sharedPreferences.getString(key, null))
                }
                "key3" -> {
                    println("hello: " + sharedPreferences.getBoolean(key, false))

                }
            }
        }

        val logout: Preference = findPreference(getString(R.string.pref_logout))
        logout.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference): Boolean {
                Log.d(TAG, "User login out")
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, RegisterActivity::class.java)
                activity?.startActivity(intent)

                return true
            }
        }

        val manage: Preference = findPreference(getString(R.string.pref_manage))
        manage.onPreferenceClickListener = object : Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                val intent = Intent(activity, ManageSpotActivity::class.java)
                activity?.startActivity(intent)
                return true
            }

        }


    }

    companion object {
        private const val TAG = "SettingsFragment"
        fun newInstance(instance: Int): SettingsFragment {
            val args = Bundle()
            args.putInt(BaseFragment.ARGS_INSTANCE, instance)
            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_settings, container, false)
//        val button = view.findViewById(R.id.button) as Button
//        button.setOnClickListener{
//            Log.d(TAG, "Clicked")
//        }
//        return view
//    }
}
