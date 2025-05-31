package io2.hobbymatch.hobby.data

import io2.hobbymatch.hobby.data.remote.HobbyApiService
import io2.hobbymatch.hobby.domain.Hobby
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HobbyRepository(
    private val apiService: HobbyApiService,
) {
    private val _hobbies = MutableStateFlow<List<Hobby>>(emptyList())
    val hobbies: StateFlow<List<Hobby>> = _hobbies.asStateFlow()
    
    private var cachedHobbies: List<Hobby>? = null
    
    suspend fun getHobbies(): List<Hobby> {
        // Jeśli mamy już pobrane hobby w pamięci podręcznej, zwróć je
        cachedHobbies?.let { return it }
        
        // W przeciwnym razie pobierz z API
        val fetchedHobbies = apiService.getHobbies()
        cachedHobbies = fetchedHobbies
        _hobbies.value = fetchedHobbies
        return fetchedHobbies
    }
    
    suspend fun refreshHobbies(): List<Hobby> {
        val fetchedHobbies = apiService.getHobbies()
        cachedHobbies = fetchedHobbies
        _hobbies.value = fetchedHobbies
        return fetchedHobbies
    }
}
