package io2.hobbymatch.activity.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import io2.hobbymatch.activity.domain.Activity
import io2.hobbymatch.activity.domain.Hobby
import io2.hobbymatch.login.data.local.realm.LoginMongoDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class HobbyDto(
    val name: String
)

class ActivityViewModel(private val loginMongoDB: LoginMongoDB) : ScreenModel {

    private val baseUrl = "http://172.20.10.3:8080"
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private var jwtToken: String? = null
    private var hobbies: MutableList<Hobby> = mutableListOf()

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<List<Activity>> = _activities

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        screenModelScope.launch {
            try {
                jwtToken = loginMongoDB.loadJwtToken()
                println("✅ Loaded JWT Token: $jwtToken")

                println("Fetching hobbies...")
                val response = httpClient.get("$baseUrl/api/hobbies") {
                    headers {
                        append("Authorization", "Bearer $jwtToken")
                    }
                }

                //val responseBody = response.body<List<Hobby>>() // Replace with your DTO
                val responseBody = response.body<List<HobbyDto>>()

                println("✅ Response: $responseBody")
            } catch (e: Exception) {
                println("❌ Failed to load JWT Token: ${e.message}")
            }
            try {
                loadActivities()
            }
            catch(e: Exception) {
                println(e)
            }
        }
    }

    private fun loadActivities() {
        screenModelScope.launch {
            _isLoading.value = true
            try {
                jwtToken = loginMongoDB.loadJwtToken()
                println("✅ Loaded JWT Token: $jwtToken")

                val response = httpClient.get("$baseUrl/api/activities") {
                    headers {
                        append("Authorization", "Bearer $jwtToken")
                    }
                }

                val activityList = response.body<List<Activity>>() // lub ActivityDto jeśli masz konwersję
                _activities.value = activityList
                println("✅ Loaded ${activityList.size} activities")
            } catch (e: Exception) {
                println("❌ Error fetching activities: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Dodatkowo, jeśli chcesz przeładować ręcznie z UI
    fun refreshActivities() {
        loadActivities()
    }

}