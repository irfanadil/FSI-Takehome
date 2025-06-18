package com.example.hairsalonappointments.ui.booking.listing

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hairsalonappointments.databinding.ItemBookingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BookingAdapter(
    private val onAppointmentClick: (String) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    private var appointments = emptyList<String>()
    private val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        return BookingViewHolder(ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: BookingViewHolder,
        position: Int
    ) {
        val appointment =appointments[position]
        holder.binding.textStartTime.text = appointment
        holder.binding.bookButton.setOnClickListener {
            onAppointmentClick(appointment)
        }
    }

    override fun getItemCount(): Int = appointments.size

    fun submitList(newAppointments: List<String>) {
        Log.e("TAG" , "newAppointments.size ="+newAppointments.size.toString())
        appointments = newAppointments
        Log.e("TAG" ,"appointments.size ="+appointments.size.toString())
        notifyDataSetChanged()
    }


    inner class BookingViewHolder(
        val binding: ItemBookingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

    }
}