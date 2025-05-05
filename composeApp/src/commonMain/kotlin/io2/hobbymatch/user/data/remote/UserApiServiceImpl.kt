package io2.hobbymatch.user.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io2.hobbymatch.network.ApiConfig
import io2.hobbymatch.user.domain.UpdateUserRequest
import io2.hobbymatch.user.domain.User

class UserApiServiceImpl(
    private val httpClient: HttpClient,
    private val apiConfig: ApiConfig
) : UserApiService {

    override suspend fun getAuthenticatedUser(): User {
        return httpClient.get {
            url(apiConfig.getEndpoint(UserApiEndpoints.GET_ME))
        }.body()
    }

    override suspend fun getUserById(userId: String): User {
        return httpClient.get {
            url(apiConfig.getEndpoint(UserApiEndpoints.GET_USER.replace("{userId}", userId)))
        }.body()
    }

    override suspend fun updateAuthenticatedUser(requestBody: UpdateUserRequest): User {
        return httpClient.put {
            url(apiConfig.getEndpoint(UserApiEndpoints.UPDATE_ME))
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    override suspend fun updateUserById(userId: String, requestBody: UpdateUserRequest): User {
        return httpClient.put {
            url(apiConfig.getEndpoint(UserApiEndpoints.UPDATE_USER.replace("{userId}", userId)))
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }
}