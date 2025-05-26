package io2.hobbymatch.hobby.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import io2.hobbymatch.hobby.domain.Hobby
import io2.hobbymatch.network.ApiConfig

class HobbyApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : HobbyApiService {

    override suspend fun getHobbies(): List<Hobby> {
        return httpClient.get {
            url(apiConfig.getEndpoint(HobbyApiEndpoints.BASE_PATH))
        }.body()
    }
}