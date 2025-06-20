package com.example.hairsalonappointments.ui.appointments
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.MockApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.collections.filter

class MainViewModel : ViewModel(){

    private val _allAppointmentsMutableStateFlow = MutableStateFlow<List<Appointment>>(emptyList())
    val allAppointmentsStateFlow : StateFlow<List<Appointment>>  = _allAppointmentsMutableStateFlow
    private val searchQueryFlow = MutableStateFlow<String>("")

    fun loadAllAppointments(){
        viewModelScope.launch(Dispatchers.IO) {
            _allAppointmentsMutableStateFlow.value = (MockApiService.getTodaysAppointments())
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun observeSearch(): Flow<List<Appointment>> {
        return searchQueryFlow
            .debounce(500) // Wait for typing to stop...Respond slowly even if user typing fast..
            .flatMapLatest{ query ->
                flow {
                    val filtered = allAppointmentsStateFlow.value.filter { item ->
                        item.clientName.contains(query, true) ||
                                item.stylistName.contains(query, true)
                    }
                    emit(filtered)
                }
            }
    }

    fun onQueryChanged(query: String) {
        searchQueryFlow.value = query
    }
}