package com.example.khoi.parkingapp.bean

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.example.khoi.parkingapp.R

class SuccessDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Get the layout inflater
            val inflater = requireActivity().layoutInflater

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.dialog_success, null))
                // Add action buttons
                .setPositiveButton(
                    R.string.ok
                ) { dialog, id ->
                    println("ok")
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
