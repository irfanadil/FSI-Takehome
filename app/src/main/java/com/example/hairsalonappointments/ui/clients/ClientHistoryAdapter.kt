package com.example.hairsalonappointments.ui.clients

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.databinding.ItemAppointmentBinding
import com.example.hairsalonappointments.databinding.ItemClientPreviousAppointmentBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ClientHistoryAdapter(
    private val onAppointmentClick: (Appointment) -> Unit
) : RecyclerView.Adapter<ClientHistoryAdapter.AppointmentViewHolder>() {
    
    private var appointments = emptyList<Appointment>()
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    
    /**
     * TODO: Implement this function
     * 
     * Create and return a ViewHolder.
     * - Inflate the item_appointment layout using view binding
     * - Return a new AppointmentViewHolder instance
     * 
     * @param parent The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new ViewHolder that holds a View for an appointment item
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        // TODO: Implement view holder creation
        // Hint: Use ItemAppointmentBinding.inflate(...)
        return AppointmentViewHolder(ItemClientPreviousAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        //throw NotImplementedError("Candidate needs to implement onCreateViewHolder()")
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        // TODO: Implement data binding
        // Get the appointment at this position
        // Bind all the data to the views
        // Set click listener
        val appointment =appointments[position]

        holder.binding.textViewStatus.text = appointment.status.name
        holder.binding.textViewTime.text = appointment.appointmentTime.toString()
        holder.binding.textViewClientName.text= appointment.clientName
        holder.binding.textViewService.text  = appointment.serviceType.displayName
        holder.binding.textViewStylist.text  = appointment.stylistName
        holder.itemView.setOnClickListener {
            onAppointmentClick(appointment)
        }
    }
    
    override fun getItemCount(): Int = appointments.size

    fun submitList(newAppointments: List<Appointment>) {
        appointments = newAppointments
        notifyDataSetChanged()
    }
    

    inner class AppointmentViewHolder(
        val binding: ItemClientPreviousAppointmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(appointment: Appointment, onClickListener: (Appointment) -> Unit) {
            // This method can be used in onBindViewHolder
            // to keep the binding logic organized
        }
    }
}