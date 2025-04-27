package io2.hobbymatch.network.endpoints

object UserApiEndpoints {
    const val BASE_PATH = "users"

    const val GET_USER = "$BASE_PATH/{userId}"
    const val UPDATE_USER = "$BASE_PATH/{userId}"
}