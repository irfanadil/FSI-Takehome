package com.example.hairsalonappointments.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairsalonappointments.data.MockApiService
import com.example.hairsalonappointments.data.ServiceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class BookingSlotData(
    val timeSlot: String,
    val clientName: String,
    val clientPhone: String,
    val stylistName: String,
    val stylistService: ServiceType
)

class BookingViewModel: ViewModel() {

    private  val apiService = MockApiService

    private val _newBookingCompletedBoolean = MutableSharedFlow<Boolean>()
    val newBookingCompletedBoolean: SharedFlow<Boolean> = _newBookingCompletedBoolean.asSharedFlow()

    private val _availableSlots = MutableStateFlow<List<String>>(emptyList())
    val availableSlots: StateFlow<List<String>> = _availableSlots

    init {
        loadAvailableSlots()
    }
    
    fun loadAvailableSlots(){
        viewModelScope.launch(Dispatchers.IO){
            _availableSlots.value = apiService.getAvailableSlots()
        }
    }

    fun reserveAppointmentSlot(bookingData:BookingSlotData){
        viewModelScope.launch(Dispatchers.IO) {
            apiService.bookAppointmentSlot(timeSlot = bookingData.timeSlot,
                clientName= bookingData.clientName,
                clientPhone= bookingData.clientPhone,
                stylistName= bookingData.stylistName,
                serviceType= bookingData.stylistService)?.let {
                    _newBookingCompletedBoolean.emit(true)
                    _availableSlots.value = apiService.getAvailableSlots()
            }
                ?:_newBookingCompletedBoolean.emit(false)
        }
    }
}



