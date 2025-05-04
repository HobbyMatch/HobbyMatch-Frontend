package io2.hobbymatch.business.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io2.hobbymatch.business.data.BusinessClientRepository
import io2.hobbymatch.business.domain.BusinessClient
import io2.hobbymatch.business.domain.Venue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BusinessClientViewModel(
    private val repository: BusinessClientRepository
) : ViewModel() {

    private val _state = MutableStateFlow<BusinessClient?>(null)
    val state: StateFlow<BusinessClient?> = _state

    fun addObject(businessObject: Venue) {
        viewModelScope.launch {
            val client = _state.value ?: return@launch
            repository.addVenue(client.id, businessObject)
        }
    }
}