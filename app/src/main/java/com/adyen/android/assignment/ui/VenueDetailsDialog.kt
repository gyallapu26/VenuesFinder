package com.adyen.android.assignment.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.adyen.android.assignment.databinding.FragmentVenueDetailsBinding
import com.adyen.android.assignment.domain.ddd.Venue

class VenueDetailsDialog : DialogFragment() {

    private var _binding: FragmentVenueDetailsBinding? = null
    private val binding get() = _binding!!
    private var venue: Venue? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentVenueDetailsBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(false)
            .create()
        with(dialog.window) {
            this?.let {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
        venue = arguments?.getParcelable<Venue>(VENUE_TAG)
        initClickListeners()
        renderUi()
        return dialog

    }

    @SuppressLint("SetTextI18n")
    private fun renderUi() {
        venue?.let {
            with(binding) {
                titleTv.text = it.name
                addressTv.text = it.address
                coordinatesTv.text = it.coordinates.latitude.toString() + ", " + it.coordinates.longitude.toString()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _binding = null
    }

    private fun initClickListeners() {
        binding.okButton.setOnClickListener {
            dismiss()
        }
    }


    companion object {
        val TAG = VenueDetailsDialog::class.java.canonicalName
        private val VENUE_TAG = Venue::class.java.canonicalName
        fun getInstance(venue: Venue): VenueDetailsDialog = VenueDetailsDialog().apply {
            arguments = Bundle().apply {
                putParcelable(VENUE_TAG, venue)
            }
        }
    }

}