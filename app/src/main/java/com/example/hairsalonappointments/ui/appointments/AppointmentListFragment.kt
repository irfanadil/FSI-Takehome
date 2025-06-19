package com.example.hairsalonappointments.ui.appointments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.R
import com.example.hairsalonappointments.adapters.AppointmentAdapter
import com.example.hairsalonappointments.adapters.NavigateTo
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.AppointmentStatus
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentAppointmentListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

/**
 * Fragment for displaying the list of appointments
 *
 * TASK FOR CANDIDATE:
 * Implement the core functionality for this fragment.
 *
 * Requirements:
 * - Load appointments from MockApiService
 * - Display them in a RecyclerView
 * - Handle empty states
 * - Implement filter toggle (All vs Available)
 * - Navigate to detail screen on item click
 * - Handle loading and error states appropriately
 */
class AppointmentListFragment : Fragment() {

    private var _binding: FragmentAppointmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AppointmentAdapter
    private lateinit var apiService: MockApiService
    private var allAppointments = emptyList<Appointment>()
    private var showingAllAppointments = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilterToggle()
        setupFilterSearch()
        loadAppointments()
        addBookingFabIcon()
    }

    private fun setupRecyclerView() {
        adapter = AppointmentAdapter { appointment , navigateTo ->
            onAppointmentClick(appointment, navigateTo)
        }

        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AppointmentListFragment.adapter
        }
    }

    private fun setupFilterToggle() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipAll -> {
                    showingAllAppointments = true
                    updateAppointmentList()
                }
                R.id.chipAvailable -> {
                    showingAllAppointments = false
                    updateAppointmentList()
                }
            }
        }
    }


    @OptIn(FlowPreview::class)
    private fun setupFilterSearch() {
        binding.searchFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val str: String = s.toString()
                /*
                   val appointmentsToShow =  allAppointments.filter {
                       it.clientName.contains(str,true) || it.stylistName.contains(str,true)
                   }
                */
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                        searchAppointment(str).collect { filteredAppointment ->
                            adapter.submitList(filteredAppointment)
                            updateEmptyState(filteredAppointment.isEmpty())
                        }
                    }
                }
            }
        })
    }

    private fun searchAppointment(query: String): Flow<List<Appointment>> = flow {
        val filtered = allAppointments.filter { item ->
            item.clientName.contains(query, true) ||
                    item.stylistName.contains(query, true)
        }
        emit(filtered)
    }.debounce(3000).distinctUntilChanged()
        .flowOn(Dispatchers.Default)// Process filtering off UI thread


    private fun addBookingFabIcon(){
        binding.fab.setOnClickListener {
            findNavController().navigate(
                R.id.action_appointmentListFragment_to_appointmentBookFragment
            )
        }
    }



    /**
     * TODO: Implement this function
     *
     * Load appointments from the MockApiService.
     * - Initialize the apiService
     * - Call getTodaysAppointments()
     * - Update the UI based on results
     * - Show empty state if no appointments
     * - Handle any errors gracefully
     */
    private fun loadAppointments() {
        // TODO: Implement appointment loading
        // 1. Initialize MockApiService
        // 2. Get today's appointments
        // 3. Store in allAppointments
        // 4. Update the RecyclerView
        // 5. Handle empty state
        apiService = MockApiService
        allAppointments = listOf()
        allAppointments = allAppointments + apiService.getTodaysAppointments()
        Log.e("TAG", "ALL-Appointments =" + allAppointments.size.toString())
        if (allAppointments.isNotEmpty())
            adapter.submitList(allAppointments)
    }

    /**
     * TODO: Implement this function
     *
     * Handle appointment item clicks.
     * - Navigate to AppointmentDetailFragment
     * - Pass the appointment ID as an argument
     * - Use Navigation component
     *
     * @param appointment The clicked appointment
     */
    private fun onAppointmentClick(appointment: Appointment , navigateTo: NavigateTo) {
        when(navigateTo){
            NavigateTo.CLIENT_PREVIOUS_APPOINTMENT_SCREEN ->
                findNavController().navigate(
                    R.id.action_appointmentListFragment_to_clientHistoryFragment,
                    bundleOf("phoneNumber" to appointment.clientPhone)
                )
            NavigateTo.BOOKING_DETAIL_SCREEN ->
                findNavController().navigate(
                    R.id.action_appointmentListFragment_to_appointmentDetailFragment,
                    bundleOf("appointmentId" to appointment.id)
                )
            NavigateTo.STYLIST_SCREEN -> {

            }
        }
    }

    /**
     * Update the displayed appointment list based on current filter
     */
    private fun updateAppointmentList() {
        val appointmentsToShow = if (showingAllAppointments) {
            allAppointments
        } else {
            // Show only pending and confirmed appointments (available for service)
            allAppointments.filter {
                it.status == AppointmentStatus.PENDING ||
                        it.status == AppointmentStatus.CONFIRMED
            }
        }
        adapter.submitList(appointmentsToShow)
        updateEmptyState(appointmentsToShow.isEmpty())
    }

    /**
     * Update the empty state visibility
     *
     * @param isEmpty Whether the list is empty
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.textViewEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerViewAppointments.visibility = if (isEmpty) View.GONE else View.VISIBLE

        if (isEmpty) {
            binding.textViewEmpty.text = if (showingAllAppointments) {
                "No appointments scheduled for today"
            } else {
                "No available appointments"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}