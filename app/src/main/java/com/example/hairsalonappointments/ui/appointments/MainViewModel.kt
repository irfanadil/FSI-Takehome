package com.example.hairsalonappointments.ui.appointments
import androidx.lifecycle.ViewModel
import com.example.hairsalonappointments.data.Appointment
import com.example.hairsalonappointments.data.MockApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlin.collections.filter

class MainViewModel : ViewModel(){


    val appointmentsListStateFlow = MockApiService.getTodaysAppointments()

    private val searchQueryFlow = MutableStateFlow<String>("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun observeSearch(): Flow<List<Appointment>> {
        return searchQueryFlow
            .debounce(500) // Wait for typing to stop...Respond slowly even if user typing fast..
            .flatMapLatest{ query ->
                flow {
                    val filtered = appointmentsListStateFlow.value.filter { item ->
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