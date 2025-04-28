package io2.hobbymatch.network.endpoints

object UserApiEndpoints {
    const val BASE_PATH = "users"

    // OLD VERSION
    const val GET_USER = "$BASE_PATH/{userId}"
    const val UPDATE_USER = "$BASE_PATH/{userId}"

    // NEW VERSION
    const val GET_ME = "$BASE_PATH/me"
    const val UPDATE_ME = "$BASE_PATH/me"
}