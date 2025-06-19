package com.example.hairsalonappointments.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.databinding.FragmentClientHistoryBinding

import kotlin.getValue

class ClientHistoryFragment : Fragment() {

    private var _binding: FragmentClientHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ClientHistoryAdapter
    private var clientPreviousAppointment = emptyList<Appointment>()

    private val args: ClientHistoryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadClientHistory()
    }

    private fun setupRecyclerView() {
        adapter = ClientHistoryAdapter{}
        binding.recyclerViewAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ClientHistoryFragment.adapter
        }
    }

    private fun loadClientHistory() {

        clientPreviousAppointment = MockApiService.getClientHistory(args.phoneNumber)
        if (clientPreviousAppointment.isNotEmpty()) {
            binding.textViewTitle.text = clientPreviousAppointment.first().clientName+ " Appointments"
            adapter.submitList(clientPreviousAppointment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}