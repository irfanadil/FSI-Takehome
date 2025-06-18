package com.example.hairsalonappointments.ui.booking.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.hairsalonappointments.data.ServiceType
import com.example.hairsalonappointments.databinding.FragmentDialogBookingInputFormBinding
import com.example.hairsalonappointments.ui.booking.BookingSlotData
import com.example.hairsalonappointments.ui.booking.BookingViewModel
import kotlin.getValue

class BookingInputDialogFragment: DialogFragment()  {

    private var _binding: FragmentDialogBookingInputFormBinding? = null
    private val binding get() = _binding!!
    private var selectedServiceType: ServiceType?=null
    private lateinit var timeSlotSelected: String
    private val viewModel: BookingViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogBookingInputFormBinding.inflate(layoutInflater)
        setupServiceTypeSpinner()

        return AlertDialog.Builder(requireContext())
            .setTitle("Enter Booking Details")
            .setView(binding.root)
            .setPositiveButton("Submit") { _, _ ->
                val clientName = binding.clientName.text?.toString()?.trim() ?: ""
                val clientNumber = binding.clientNumber.text?.toString()?.trim() ?: ""
                val stylistName = binding.stylistName.text?.toString()?.trim() ?: ""
                //val serviceType = ServiceType.getServiceTypeFromDisplayName(selectedServiceType)
                if (validateInputs()) {
                    selectedServiceType?.let { it->
                        viewModel.reserveAppointmentSlot(
                            BookingSlotData(
                                timeSlotSelected,
                                clientName,
                                clientNumber,
                                stylistName,
                                it
                            )
                        )
                    }?:Toast.makeText(requireContext(), "Service type not found...", Toast.LENGTH_SHORT).show()


                } else {
                    Toast.makeText(requireContext(), "Error in the booking form...", Toast.LENGTH_SHORT).show()
                    // Keep the dialog open if validation fails
                    //show(childFragmentManager, TAG)
                    // When showing:
                    if (!isDialogShowing) {
                        show(childFragmentManager, TAG)
                    }
                }
            }
            .setNegativeButton("Cancel") { _, _ -> dismiss() }
            .create()
    }

    private fun setupServiceTypeSpinner() {
        val serviceTypes = ServiceType.getAllFormattedServices()
        val adapter = ServiceTypeSpinnerAdapter(requireContext(), serviceTypes)
        binding.stylistService.setAdapter(adapter)
        binding.stylistService.setOnItemClickListener { _, _, position, _ ->
            selectedServiceType = ServiceType.entries[position]
            Log.e ("ServiceType" , selectedServiceType.toString())

        }
    }

    companion object {
        const val TAG = "BookingInputDialogFragmentTag"
        private var isDialogShowing = false
        fun newInstance(slotTime:String) = BookingInputDialogFragment().apply {
            isDialogShowing = true
            timeSlotSelected = slotTime
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isDialogShowing = false
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (binding.clientName.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Client Name can not be empty...", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.clientNumber.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Client Number can not be empty...", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (binding.stylistName.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Stylist name can not be empty...", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if(selectedServiceType==null){
            Toast.makeText(requireContext(), "Service type not found...", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }


}