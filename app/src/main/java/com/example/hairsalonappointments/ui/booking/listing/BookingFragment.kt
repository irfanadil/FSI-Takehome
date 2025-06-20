package com.example.hairsalonappointments.ui.booking.listing

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentAppointmentBookingListBinding
import com.example.hairsalonappointments.ui.booking.BookingViewModel
import com.example.hairsalonappointments.ui.booking.dialogs.BookingInputDialogFragment
import kotlinx.coroutines.launch

class BookingFragment : Fragment()
{

    private var _binding: FragmentAppointmentBookingListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BookingAdapter
    private var availableAppointments = emptyList<String>()

    private val viewModel: BookingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBookingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeAvailableSlots()
        observeNewAppointmentSubmission()
    }

    // observe any changes to available slots and update in the UI...
    private fun observeAvailableSlots() {
        availableAppointments = listOf()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.availableSlots.collect { availableAppointments->
                    Log.e("TAG", "ALL-Appointments =$availableAppointments")
                    adapter.submitList(availableAppointments)
                }
            }
        }
    }

    // observe new appointment has been reserved or error has been received....
    private fun observeNewAppointmentSubmission(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newBookingCompletedBoolean.collect { result ->
                   when(result){
                       true -> Toast.makeText(requireContext(), "Appointment booked successfully...", Toast.LENGTH_SHORT).show()
                       false ->  Toast.makeText(requireContext(), "Appointment can not be booked...", Toast.LENGTH_SHORT).show()
                   }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = BookingAdapter { appointment ->
            onAppointmentClick(appointment)
        }
        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BookingFragment.adapter
        }
    }

    private fun onAppointmentClick(appointmentTime: String) {
        if(appointmentTime.isNotEmpty()) {
            BookingInputDialogFragment.Companion.newInstance(appointmentTime)
                .show(parentFragmentManager, BookingInputDialogFragment.Companion.TAG)
        }
        else
            Toast.makeText(requireContext(), "Time slot can not be empty...", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}